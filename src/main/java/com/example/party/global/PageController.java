package com.example.party.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page")
public class PageController {

	//회원가입페이지 호출
	@RequestMapping("/signup")
	public ModelAndView showSignUpPage() {
		return new ModelAndView("signup"); // signup.html 파일 이름

	}

	//로그인 페이지 호출
	@RequestMapping("/loginPage")
	public ModelAndView showLoginPage() {
		return new ModelAndView("login"); // login.html 파일 이름

	}

	//index 페이지 호출
	@GetMapping("/indexPage")
	public ModelAndView showIndexPage() {
		return new ModelAndView("index"); // signup.html 파일 이름
	}

	//채팅 페이지 호출
	@GetMapping("/chatPage")
	public ModelAndView showChatPage() {
		return new ModelAndView("chat"); //view할 html의 파일이름 넣어주세요

	}

	//마이페이지-블락 호출
	@GetMapping("/myPage/block")
	public ModelAndView showMyPageBlock() {
		return new ModelAndView("mypage-block"); //view할 html의 파일이름 넣어주세요

	}

	//마이페이지-partypost-apply 호출
	@GetMapping("/myPage/partypost/apply")
	public ModelAndView showMyPagePartyPostApply() {
		return new ModelAndView("mypage-partypost-apply"); //view할 html의 파일이름 넣어주세요
	}

	//마이페이지-partypost-created 호출
	@GetMapping("/myPage/partypost/created")
	public ModelAndView showMyPagePartyPostCreated() {
		return new ModelAndView("mypage-partypost-created"); //view할 html의 파일이름 넣어주세요

	}

	//마이페이지-partypost-like 호출
	@GetMapping("/myPage/partypost/like")
	public ModelAndView showMyPagePartyPostlike() {
		return new ModelAndView("mypage-partypost-like"); //view할 html의 파일이름 넣어주세요

	}

	//마이페이지-profile 호출
	@GetMapping("/myPage/profile")
	public ModelAndView showMyPageProfile() {
		return new ModelAndView("mypage-profile"); //view할 html의 파일이름 넣어주세요
	}

	//others-profile 호출
	@GetMapping("/others/profile")
	public ModelAndView showOthersProfile() {
		return new ModelAndView("others-profile"); //view할 html의 파일이름 넣어주세요

	}

	//partypost-detail 호출
	@GetMapping("/partypost")
	public ModelAndView showPartyPostDetail() {
		return new ModelAndView("partypost-detail"); //view할 html의 파일이름 넣어주세요

	}

	//partypost-edit 호출
	@GetMapping("/partypost/edit")
	public ModelAndView showPartyPostEdit() {
		return new ModelAndView("partypost-edit"); //view할 html의 파일이름 넣어주세요

	}

	//partypost-writer 호출
	@GetMapping("/partypost/writer")
	public ModelAndView showPartyPostWriter() {
		return new ModelAndView("partypost-writer"); //view할 html의 파일이름 넣어주세요

	}

	//search-result 호출
	@GetMapping("/search")
	public ModelAndView showSearchResult() {
		return new ModelAndView("search-result"); //view할 html의 파일이름 넣어주세요

	}

	/// 혹시 없는 경우 이 밑에 추가해주세요 . 어드민은 추후 추가 예정 ///
	//admin partypostlisk호출
	@GetMapping("/AdminPartyPostList")
	public ModelAndView showAdminPartyPostList() {
		return new ModelAndView("admin-partypost-list"); //view할 html의 파일이름 넣어주세요

	}

	//mypage partypostreport list호출
	@GetMapping("/myPage/MypagePartyPostReport")
	public ModelAndView showMypagePartyPostReport() {
		return new ModelAndView("mypage-partypost-report"); //view할 html의 파일이름 넣어주세요

	}
}
