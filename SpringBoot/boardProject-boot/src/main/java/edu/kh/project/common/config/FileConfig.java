package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
@PropertySource("classpath:/config.properties")
public class FileConfig implements WebMvcConfigurer{
	
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold; // 파일을 디스크에 쓸 때까지의 임계값
	
	@Value("${spring.servlet.multipart.max-file-size}")
	private long maxFileSize; // 개별 파일당 최대 크기
	
	@Value("${spring.servlet.multipart.max-request-size}")
	private long maxRequestSize; // HTTP 요청당 최대 크기
	
	@Value("${spring.servlet.multipart.location}")
	private String location; // 파일의 임시 저장 경로
	
	@Value("${my.images.connect-path}")
	private String connectPath; // 외부 폴더로 연결되는 요청 주소
	
	@Value("${my.images.resource-path}")
	private String resourcePath; // 연결되는 외부 폴더 경로
	
	
	// 요청 주소가 /images로 시작할 때 외부 경로 접근하기 ( WebMvcConfigurer 상속)
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
//		String connectPath ="/images/**";
//		String resourcePath = "file:///D:/uploadImages/";
		registry.addResourceHandler(connectPath)
        .addResourceLocations(resourcePath);
	}
	
	
	
	// MultipartResolver 설정 bean 생성
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold)); // 파일을 디스크에 쓸 때까지의 임계값(기본값 0B)
		// 디스크에 저장하지 않고 메모리에 유지하도록 허용하는 바이트 단위의 최대 용량을 설정.
		
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize)); // 개별 파일당 최대 크기(기본값 1MB)
		
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize)); // 요청당 최대 크기 (기본값 10MB)
		
		factory.setLocation(location); // 임시 저장 경로
		
		return factory.createMultipartConfig();
	}
	
	// MultipartResolver bean 생성
	@Bean
	public MultipartResolver multipartResolver() {
		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
		return multipartResolver;
	}


	
	
	
}
