package site.moregreen.basic.controller;

import java.util.List;
import java.util.stream.Collectors;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import site.moregreen.basic.command.FundingDto;
import site.moregreen.basic.funding.FundingService;
import site.moregreen.basic.util.Criteria;

@Controller
@RequestMapping("/funding")
public class FundingController {

	@Autowired
	@Qualifier("fundingService")
	FundingService fundingService;

	
	
	@GetMapping("/fundingList")
	public String fundingList(@Valid FundingDto dto, Errors errors, Model model, HttpSession session, 
		  	  @RequestParam("file") List<MultipartFile> files, Criteria cri) {
		String f_num = (String)session.getAttribute("f_num");
		cri.setF_num(f_num);
		
		List<FundingDto> list = fundingService.retriveFundingList(cri); //데이터
		int total = fundingService.retrieveTotal(cri); //전체게시글수
		//PageDto pageDto= new PageDto(cri, total); //페이지네이션
		
		model.addAttribute("list", list);
		//model.addAttribute("pageDto", pageDto);
		
		return "funding/fundingList";
	}
	
	
	

	@GetMapping("fundingDetail")
	public String fundingDetail(@RequestParam("f_num") int f_num,  Model mode) {
		
		return "funding/fundingDetail";
	}

		
	@GetMapping("fundingReg")
	public String fundingReg() {
		return "funding/fundingReg";
	}

	@PostMapping("/fundingForm")
	public String fundingForm(@Valid FundingDto dto, Errors errors, Model model,
						  	  @RequestParam("file") List<MultipartFile> files) {
		
		System.out.println("================" + dto.getM_num());
		if(errors.hasErrors()) {
			List<FieldError> list = errors.getFieldErrors();
			for(FieldError err : list) {
				if(err.isBindingFailure() ) {
					model.addAttribute("valid_" + err.getField(), "형식을 일치 시켜주세요");
				}else {
					model.addAttribute("valid_" + err.getField(), err.getDefaultMessage());
				}
			}
		model.addAttribute("dto", dto);	
		return "funding/fundingReg";
		}
		
		files = files.stream().filter( (f) -> !f.isEmpty()).collect(Collectors.toList());
		//이미지파일검증
		for(MultipartFile f : files ) {
			if( f.getContentType().contains("image") == false ) { //이미지가 아닌경우 다시 등록화면으로
				model.addAttribute("dto", dto);
				model.addAttribute("valid_files", "이미지 형식만 등록 가능합니다");
				return "funding/fundingReg";
			}
		}
		
		int result = fundingService.addFunding(dto, files);
		return "redirect:/funding/fundingList";

	}

	@GetMapping("fundingPurchase")
	public String fundingPurchase() {
		return "funding/fundingPurchase";
	}

	@GetMapping("fundingPurchaseResult")
	public String fundingPurchaseResult() {
		return "funding/fundingPurchaseResult";
	}

}
