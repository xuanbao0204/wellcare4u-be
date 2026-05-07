package vn.wellcare4u.controllers.common;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.PageDTO;
import vn.wellcare4u.models.dto.doctor.DoctorDTO;
import vn.wellcare4u.models.dto.doctor.TimeSlotDTO;
import vn.wellcare4u.models.request.DoctorListRequest;
import vn.wellcare4u.services.DoctorService;
import vn.wellcare4u.services.TimeSlotService;

@RestController
@RequestMapping("/api/v1/list")
public class ListingAPI {

	@Autowired
	DoctorService doctorServ;
	
	@GetMapping("/doctors")
	public ApiResponse<PageDTO<DoctorDTO>> getAllDoctor(DoctorListRequest req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<DoctorDTO> p = doctorServ.findAllDoctorPage(pageable, req);
        
        PageDTO<DoctorDTO> result = PageDTO.<DoctorDTO>builder()
                .content(p.getContent())
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .last(p.isLast())
                .build();
		return ApiResponse.<PageDTO<DoctorDTO>>builder()
				.status(HttpStatus.OK.value())
				.message("Lấy sanh sách thành công")
				.data(result)
				.build();
	}
	
	@Autowired
	private TimeSlotService timeslotServ;
	
	@GetMapping("/doctor/{doctorId}/available-slots")
	public ApiResponse<List<TimeSlotDTO>> getDoctorAvailableByIdAndDate(
	        @PathVariable() Long doctorId,
	        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	    List<TimeSlotDTO> slots = timeslotServ.getAvailableSlots(doctorId, date);

	    return ApiResponse.<List<TimeSlotDTO>>builder()
	            .status(HttpStatus.OK.value())
	            .message("Lấy danh sách slot cho ngày " + date + " thành công")
	            .data(slots)
	            .build();
	}
}

