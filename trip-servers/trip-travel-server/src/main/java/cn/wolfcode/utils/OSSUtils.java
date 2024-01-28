package cn.wolfcode.utils;

import cn.wolfcode.config.OSSProperties;
import com.aliyun.oss.OSS;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 需求：当前类中有方法需要获取 Spring 容器中的对象，但是该类无法交给 Spring 容器管理
 */
public class OSSUtils {

    public static String uploadFile(String path, MultipartFile file) {
        // 从 Spring 容器中获取对象
        OSS ossClient = SpringUtils.getBean("ossClient", OSS.class);
        OSSProperties properties = SpringUtils.getBean(OSSProperties.class);

        String filename = null;
        try {
            String originalFilename = file.getOriginalFilename();
            filename = toUUIDFileName(path, originalFilename.substring(originalFilename.lastIndexOf(".") + 1));

            InputStream inputStream = file.getInputStream();
            // 简单类型的文件上传
            ossClient.putObject(properties.getBucketName(), filename, inputStream);

            // https://cd-wolf2w-cloud.oss-cn-chengdu.aliyuncs.com/images/xxxx.jpg
            return getFullUrl(properties, filename);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadBase64ImgToOSS(String path, String base64Img) {
        try {
            String filename = toUUIDFileName(path, getBase64ImgExt(base64Img));

            // 去除前缀，保留 base64 部分编码
            base64Img = base64Img.replaceAll("data:image/.*;base64,", "");
            // base64 解码
            byte[] decode = Base64.getDecoder().decode(base64Img); // 对图片解码

            OSS ossClient = SpringUtils.getBean("ossClient", OSS.class);
            OSSProperties properties = SpringUtils.getBean(OSSProperties.class);
            // 将解码后的数据包装为二进制流对象，并上传到阿里云
            ossClient.putObject(properties.getBucketName(), filename, new ByteArrayInputStream(decode));

            return getFullUrl(properties, filename);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBase64ImgExt(String img) {
        Pattern compile = Pattern.compile("data:image/.*;base64,");
        Matcher matcher = compile.matcher(img);
        if (matcher.find()) {
            return matcher.group().replaceAll("data:image/", "").replaceAll(";base64,", "");
        }

        return "jpg";
    }

    public static boolean isBase64Img(String img) {
        Pattern compile = Pattern.compile("data:image/.*;base64,");
        Matcher matcher = compile.matcher(img);
        return matcher.find();
    }

    @NotNull
    private static String toUUIDFileName(String path, String suffix) {
        String filename;
        filename = UUID.randomUUID().toString().replaceAll("-", "");

        filename = path + "/" + filename + "." + suffix;
        return filename;
    }

    @NotNull
    private static String getFullUrl(OSSProperties properties, String filename) {
        return "https://" + properties.getBucketName() + "." + properties.getEndpoint() + "/" + filename;
    }


    public static void main(String[] args) {
        String originalFilename = "xxx.jpg";
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        System.out.println(suffix);
    }
}
