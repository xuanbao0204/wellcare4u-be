package vn.wellcare4u.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.models.ApiResponse;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadAPI {

	private final Cloudinary cloudinary;

	@Value("${cloudinary.cloud-name}")
	private String cloudName;

	@Value("${cloudinary.api-key}")
	private String apiKey;

	@GetMapping("/signature")
	public ApiResponse<Map<String, Object>> generateSignature(@RequestParam String folder,
			@RequestParam(required = false) String publicId) {
		long timestamp = System.currentTimeMillis() / 1000;

		Map<String, Object> params = new HashMap<>();
		params.put("timestamp", timestamp);
		params.put("folder", folder);

		if (publicId != null) {
			params.put("public_id", publicId);
			params.put("overwrite", true);
		}

		String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

		Map<String, Object> response = new HashMap<>();
		response.putAll(params);
		response.put("signature", signature);
		response.put("api_key", apiKey);
		response.put("cloud_name", cloudName);

		return ApiResponse.<Map<String, Object>>builder().status(200).message("OK").data(response).build();
	}

	@DeleteMapping("/file")
	public ApiResponse<String> deleteFile(@RequestParam String publicId) throws IOException {
		Map result = cloudinary.uploader().destroy(publicId, Map.of());
		return ApiResponse.<String>builder().status(200).message("Delete success").data(result.get("result").toString())
				.build();
	}
}