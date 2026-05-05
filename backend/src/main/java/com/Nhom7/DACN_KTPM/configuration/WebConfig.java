package com.Nhom7.DACN_KTPM.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
// 1. Xác định đường dẫn thư mục uploads bên ngoài
        String projectRoot = System.getProperty("user.dir");
        String uploadPath = java.nio.file.Paths.get(projectRoot, "uploads").toFile().getAbsolutePath();

        // 2. Cấu hình "Cầu nối" đa năng
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        "file:" + uploadPath + "/",      // Ưu tiên 1: Tìm trong folder uploads bên ngoài
                        "classpath:/public/images/"     // Ưu tiên 2: Tìm trong resources/public/images nội bộ
                )
                .setCachePeriod(3600); // Tối ưu hiệu suất bằng cách lưu cache 1 tiếng
    }
//trước khi thay đổi để chạy ngrok
    /*@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }*/
    //chạy ngrok
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}