package com.quote.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quote.services.UploadService;
import com.quote.vm.MessageVM;


@RestController
@RequestMapping("/ndquote/api")
public class UploadController {

	@Autowired
	UploadService uploadService;
	
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	@ResponseBody
	public MessageVM uploadZip() {
		return null;
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public MessageVM uploadZip(@RequestParam("file") MultipartFile file) throws Exception {
		return uploadService.upload(file);
	}
	
	@RequestMapping(value = "/validatesignature", method = RequestMethod.POST)
	@ResponseBody
	public MessageVM validateSignature(@RequestParam("file") MultipartFile file) {
		return uploadService.checkValidSignature(file);
	}
}