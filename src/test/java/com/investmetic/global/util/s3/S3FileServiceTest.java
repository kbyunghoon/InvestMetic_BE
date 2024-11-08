package com.investmetic.global.util.s3;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class S3FileServiceTest {

    /*
     * api요청은 postman으로 합시다.
     * */
    @Autowired
    S3FileService s3FileService;

    @Value("${cloud.aws.s3.defaultImgPath}")
    private String bucketPath; // "https://{버킷 이름}.s3.{region}.amazonaws.com/"

    // fileName만 받을 건지, ContentType이나 MimeType도 받을건지에 따라 test코드도 달라질 듯

    @Test
    @DisplayName("정상 이미지 확장자랑 사이즈 판별")
    void validatePassImageCase() {

        String fileName = "testImage.jpg";
        int size = 1000;

        //객체 URL 획득
        String s3Path = s3FileService.getS3Path(FilePath.STRATEGY_IMAGE, fileName, size);

        System.out.println("s3Path = " + s3Path);
        System.out.println(bucketPath + FilePath.STRATEGY_IMAGE.getPath() + fileName);

        assertTrue(s3Path.equals(bucketPath + FilePath.STRATEGY_IMAGE.getPath() + fileName));
        System.out.println(s3Path);
    }

    @Test
    @DisplayName("이미지 확장자 다를 때")
    void noPassImageCaseExtension() {

        String fileName = "testImage.webp"; // 확장자 불충족.
        int size = 1000; //약 1KB

        String s3Path;

        // 확장자 다르면 RuntimeException던짐.
        try {
            //throw new RuntimeException("Not Supported File");
            s3Path = s3FileService.getS3Path(FilePath.STRATEGY_IMAGE, fileName, size);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());

            assertTrue(e.getClass().equals(RuntimeException.class));
            assertTrue(e.getMessage().equals("Not Supported File"));
        }
    }

    @Test
    @DisplayName("이미지 사이즈 2MB 이상")
    void noPassImageCaseSize() {
        String fileName = "testImage.jpg"; // 확장자 충족.
        int size = 1024 * 1024 * 2; //2MB -> 2MB미만만 가능.

        String s3Path;

        // 2MB초과면  RuntimeException던짐.
        try {
            //throw new RuntimeException("Not Supported File");
            s3Path = s3FileService.getS3Path(FilePath.STRATEGY_IMAGE, fileName, size);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());

            assertTrue(e.getClass().equals(RuntimeException.class));
            assertTrue(e.getMessage().equals("Not Supported File"));
        }
    }

    @Test
    @DisplayName("substring Test")
    void substringTest() {
        String successKey = "strategy/image/testImage.jpg";
        String s3Path = "https://jrw-toyproject-imgs.s3.ap-northeast-2.amazonaws.com/strategy/image/testImage.jpg";

        String s3Key = s3Path.substring(s3Path.lastIndexOf(".com/") + 5);
        System.out.println(s3Key);
        System.out.println(successKey);

        assertTrue(s3Key.equals(successKey));

    }

    @Test
    @DisplayName("정상 엑셀 확장자랑 사이즈 판별")
    void passExcelCase() {

        String fileName = "testExcel.xlsx"; // 확장자 충족.
        int size = 1024 * 1024 * 100; //약 100MB

        //객체 URL 획득
        String s3Path = s3FileService.getS3Path(FilePath.STRATEGY_EXCEL, fileName, size);

        System.out.println("s3Path = " + s3Path);
        System.out.println(bucketPath + FilePath.STRATEGY_EXCEL.getPath() + fileName);

        assertTrue(s3Path.equals(bucketPath + FilePath.STRATEGY_EXCEL.getPath() + fileName));
        System.out.println(s3Path);
    }
}


