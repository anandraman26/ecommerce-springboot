package com.ecommerce.product.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageServeController {
		private final String uploadDir = System.getProperty("user.dir")+"/upload/products";
		@GetMapping("/products/images/{fileName}")
		public ResponseEntity<Resource> getImage(@PathVariable String fileName)throws IOException{
			Path path =  Paths.get(uploadDir, fileName);
			Resource resource  = new UrlResource(path.toUri());
			if(!resource.exists()) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
		}
}
