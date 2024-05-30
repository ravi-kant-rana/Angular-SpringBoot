package com.exam.controller;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exam.service.impl.S3StorageService;

@RestController
@CrossOrigin
@RequestMapping("/s3")
public class S3StorageController {
	
	@Autowired
	private S3StorageService s3StorageService;
	
	@PostMapping("/upload/")
	public ResponseEntity<?> uploadFile(@RequestParam(value ="file") MultipartFile file) {
		System.out.println("Hello");
		String uploadResponse=s3StorageService.uploadFile(file);
		return  ResponseEntity.ok(uploadResponse);
	}
	
	@GetMapping("/download/{fileName}")
	public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
		byte[] data=s3StorageService.downloadFile(fileName);
		return  ResponseEntity
				.ok()
				.contentLength(data.length)
				.header("Content-type", "application/octet-stream")
				.header("Content-disposition", "attachment: filename=\"" + fileName+"\"")
				.body(data);
	}
	
	@DeleteMapping("/delete/{fileName}")
	public <T> ResponseEntity<T> deleteFile(@PathVariable String  fileName){
		String deleteResponse=s3StorageService.deletFile(fileName);
		return  (ResponseEntity<T>) ResponseEntity.ok(deleteResponse);
	}

}
