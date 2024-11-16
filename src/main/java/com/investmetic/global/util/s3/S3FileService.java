package com.investmetic.global.util.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * 현재는 s3객체의 Key가 s3버킷 내의 경로입니다.
 * <br>
 * s3객체의 key를 따로 설정하고 싶으면 getGeneratePreSignedUrlRequest메서드를 수정하면 됩니다.
 * <br>
 * image metadata 받아오는 파라미터가 달라질 수 있음.(content-type)
 */
@Service
public class S3FileService {

    private final AmazonS3 amazonS3;


    @Value("${cloud.aws.s3.expiration}")
    private long apiExpiration;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName; //버킷 이름

    /**
     * "https://{버킷 이름}.s3.{region}.amazonaws.com/"
     */
    @Value("${cloud.aws.s3.defaultImgPath}")
    private String bucketPath;

    private static HashSet<String> imgExtensionSet;
    private static HashSet<String> excelExtensionSet;
    private static HashSet<String> docsExtensionSet;

    public S3FileService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
        imgExtensionSet = new HashSet<>(Arrays.asList("jpg", "jpeg", "png"));
        excelExtensionSet = new HashSet<>(Arrays.asList("xls", "xlsx"));
        docsExtensionSet = new HashSet<>(Arrays.asList("doc", "docx", "pptx", "ppt"));
    }

    /**
     * S3상에서 UserProfile이미지가 저장될 경로를 반환.
     * <br>
     * ex) "https://{버킷이름}.s3.{region}.amazonaws.com/userProfile/testImage1.jpg"
     *
     * @param fileName 사용자가 저장하려고 하는 image의 이름   ex) testImage1.jpg (확장자 포함)
     * @param size     사용자가 저장하려고 하는 image의 size (file.size시 byte값이 기본)
     * @return DB에 저장될 S3경로.
     */
    public String getS3Path(FilePath filePath, String fileName, int size) {

        //전략 엑셀.
        if (filePath.equals(FilePath.STRATEGY_EXCEL) || filePath.equals(FilePath.STRATEGY_PROPOSAL)) {
            //확장자가 틀리거나 500MB이상 인지 확인
            if (!filterExcelExtension(fileName) || !(size < 1024 * 1024 * 500)) {
                throw new RuntimeException("Not Supported File"); // 검사 불통시 예외던짐
            }

            //전략 이미지, 유저 프로필 사진
        } else if (filePath.equals(FilePath.STRATEGY_IMAGE) || filePath.equals(FilePath.USER_PROFILE)) {
            //확장자가 틀리거나 2MB이상인지 확인
            if (!filterImageExtension(fileName) || !(size < 1024 * 1024 * 2)) {
                throw new RuntimeException("Not Supported File"); // 검사 불통시 예외던짐
            }

            //공지사항.
        } else if (filePath.equals(FilePath.NOTICE)) {
            //확장자가 틀리거나 5MB이상인지 확인
            if (!filterNoticeExtension(fileName) || !(size < 1024 * 1024 * 5)) {
                throw new RuntimeException("Not Supported File"); // 검사 불통시 예외던짐
            }
        } else {

            // 아무것도 아닌경우. - 이럴 일은 없겠지만...
            throw new RuntimeException("Not Supported Type");
        }

        //객체 URL 경로 반환.(도메인 경로 포함)
        return prefixFilePath(filePath.getPath(), createUUID8() + fileName);
    }


    /**
     * 현재 파일의 확장자가 imgExtensionList에 해당하는지 확인
     *
     * @param filename 확장자가 붙은 파일 이름
     */
    private boolean filterImageExtension(String filename) {

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        // 해당하는 확장자가 있으면 true 반환.
        return imgExtensionSet.contains(extension);
    }


    /**
     * 현재 파일의 확장자가 excelExtensionList에 해당하는지 확인
     *
     * @param filename 확장자가 붙은 파일 이름
     */
    private boolean filterExcelExtension(String filename) {

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        // 해당하는 확장자가 있으면 true 반환. O(n)이지만 가독성을 위해...
        return excelExtensionSet.contains(extension);
    }


    /**
     * 현재 파일의 확장자가 imgExtensionList나 docsExtensionList에 해당하는지 확인.
     *
     * @param filename 확장자가 붙은 파일 이름.
     */
    private boolean filterNoticeExtension(String filename) {

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        //공치사항에 올릴 파일이 이미지 확장자에 해당하는지 검증.
        if (filterImageExtension(extension)) {
            return true;
        }

        //공지사항헤 올릴 파일이 doc이나 ppt 종류에 해당하는지 검증.
        return docsExtensionSet.contains(extension);

    }

    /**
     * s3 버킷 내의 경로 설정. 마지막 슬래시"/" 기준으로 앞 문자열은 폴더 구조, 뒤 문자열을 파일 이름.
     *
     * @param filePath 폴더 경로
     * @param fileName 확장자 포함 파일 이름
     */
    private String prefixFilePath(String filePath, String fileName) {
        return String.format("%s%s%s", bucketPath, filePath, fileName);
    }


    /**
     * AWS에게서 presigned url을 요청한다.
     *
     * @param filePath 클라이언트가 전달한 파일명 파라미터
     * @return presigned url경로.
     */
    public String getPreSignedUrl(String filePath) {

        // s3객체 URL에서 Key 추출(testCode에서 확인)
        // filename에 ".com/" 안들어가있으면 앞에서부터 문자 짤립니다... postman으로 test할 때 확인하세요.
        String s3Key = filePath.substring(filePath.lastIndexOf(".com/") + 5);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucketName, s3Key);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return URLDecoder.decode(url.toString(), StandardCharsets.UTF_8);
    }


    /**
     * 파일 업로드용(PUT) presigned url request 생성
     *
     * @param bucket 버킷 이름
     * @param s3Key  s3객체의 Key
     * @return presignedUrl 생성을 위한 요청 객체
     */
    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String s3Key) {

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, s3Key)
                .withMethod(HttpMethod.PUT) // Put에 대한 메서드로 presigned URL생성
//                .withKey() //s3객체의 Key도 설정 가능함.
                .withExpiration(getPreSignedUrlExpiration()); // default 15분

        // 업로드 정책을 Bucket Policy로 강제해 주면 acl 조건을 강제하여 업로드 하도록 가능하다고 함.
        // https://www.wisen.co.kr/pages/blog/blog-detail.html?idx=12022
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL, // "x-amz-acl" S3 객체의 액세스 제어 목록 에 대한 조건 (canned acl)
                CannedAccessControlList.PublicRead.toString() // public-read을 acl 파라미터로 정의.
        );

        return generatePresignedUrlRequest;
    }


    /**
     * presignedUrl의 유효기간 설정
     */
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime(); // 현재 시간
        expTimeMillis += apiExpiration; // 1000 - 1초
        expiration.setTime(expTimeMillis); //현재 시간 + 1분 까지 유효
        return expiration;
    }


    /**
     * 서버에서 이미지 삭제
     *
     * @param filePath s3들어가서 객체(file)클릭해 보면 객체 개요에서 key에 해당하는 부분
     */
    public void deleteFromS3(String filePath) {

        String s3Key = filePath.substring(filePath.lastIndexOf(".com/") + 5);

        try {
            // s3객체의 key기반 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, s3Key));
        } catch (Exception e) {
            //실패시
            throw new RuntimeException("파일을 삭제하는데 실패했습니다.");
        }
    }


    /**
     * 파일 고유 ID를 생성 return : 8자리의 UUID
     */
    private String createUUID8() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
