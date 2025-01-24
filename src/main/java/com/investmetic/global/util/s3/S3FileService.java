package com.investmetic.global.util.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * 현재는 s3객체의 Key가 s3버킷 내의 경로입니다.
 * <br>
 * s3객체의 key를 따로 설정하고 싶으면 getGeneratePreSignedUrlRequest메서드를 수정하면 됩니다.
 * <br>
 * image metadata 받아오는 파라미터가 달라질 수 있음.(content-type)
 */
@Slf4j
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


    public S3FileService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
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
        validateFile(filePath, fileName, size);
        return prefixFilePath(filePath.getPath(), createUUID8() + fileName);
    }


    public String getS3StrategyPath(FilePath filePath, Long strategyId, String fileName, int size) {
        validateFile(filePath, fileName, size);
        return prefixFilePath(filePath.getStrategyPath(strategyId), createUUID8() + fileName);
    }

    /**
     * 파일 검증 로직
     */
    private void validateFile(FilePath filePath, String fileName, int size) {
        if (!filePath.isValidExtension(fileName) || size >= filePath.getMaxSize()) {
            throw new BusinessException(ErrorCode.NOT_SUPPORTED_TYPE);
        }
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

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket,
                s3Key).withMethod(HttpMethod.PUT) // Put에 대한 메서드로 presigned URL생성
//                .withKey() //s3객체의 Key도 설정 가능함.
                .withExpiration(getPreSignedUrlExpiration()); // default 15분

        // 업로드 정책을 Bucket Policy로 강제해 주면 acl 조건을 강제하여 업로드 하도록 가능하다고 함.
        // https://www.wisen.co.kr/pages/blog/blog-detail.html?idx=12022
        generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL,
                // "x-amz-acl" S3 객체의 액세스 제어 목록 에 대한 조건 (canned acl)
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
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * s3내 폴더 삭제. AWS CLI나 S3 Batch Operations이 비용이 적다고함.
     *
     * @param strategyId ex- strategy/{strategyId}/
     * @see <a href = "https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObjects.html"> deleteObjects </a>
     */
    public void deleteStrategyFolder(Long strategyId) {

        // folderKey와 매칭되는 하위경로 객체 최대 1000개까지의 반환.
        ObjectListing objectListing = getObjectListing(FilePath.getS3StrategyKey(strategyId));

        // key 하위의 객체가 없다면 return
        if (objectListing.getObjectSummaries().isEmpty()) {
            return;
        }

        while (true) {
            // key와 version을 받을 수 있는 객체 리스트 생성.
            List<KeyVersion> keysToDelete = new ArrayList<>();

            // s3로부터 S3ObjectSummary를 가져옴(key, version)
            for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
                // 모든 객체키를 알고있다면 s3에 요청 안보내도 될 것으로 보임.
                keysToDelete.add(new KeyVersion(s3ObjectSummary.getKey()));
            }

            if (!keysToDelete.isEmpty()) {
                try {
                    // s3에서 해당 key리스트 모두 삭제. - 요청 1번.
                    deleteByKeyList(keysToDelete);

                } catch (SdkClientException e) {
                    // 삭제 오류
                    log.error("Folder Not Deleted in S3 : strategy/{}/", strategyId);
                    throw new BusinessException(ErrorCode.FILE_DELETE_FAILED);
                }
            }

            // 해당 key경로에 1000개보다 더 많은 객체가 있다면 한번더 요청.
            if (objectListing.isTruncated()) {
                objectListing = amazonS3.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
    }

    // s3KeyList 한 번에 삭제.
    public void deleteByKeyList(List<KeyVersion> keysToDelete) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keysToDelete);

        amazonS3.deleteObjects(deleteObjectsRequest);
    }

    /**
     * 해당 s3Key 하위 경로의 모든 객체 조회.
     */
    private ObjectListing getObjectListing(String folderKey) {
        try {
            return amazonS3.listObjects(bucketName, folderKey);
        } catch (SdkClientException e) {
            log.error("Loading error in S3 : {}", folderKey);
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * URL에서 S3 파일 키 추출
     */
    public S3Object extractFileFromUrl(String fileUrl) {
        //  s3Key : "strategy/3458/proposal/fb8d6a98BL_전략.xls"
        String s3Key = fileUrl.substring(fileUrl.lastIndexOf(".com/") + 5);

        log.debug("s3Key : %s".formatted(s3Key));
        // S3에서 파일 가져오기
        return amazonS3.getObject(bucketName, s3Key);
    }


    /**
     * 파일 고유 ID를 생성 return : 8자리의 UUID
     */
    private String createUUID8() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
