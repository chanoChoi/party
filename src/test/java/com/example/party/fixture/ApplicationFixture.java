// package com.example.party.fixture;
//
// import java.util.List;
//
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
//
// import com.example.party.application.entity.Application;
//
// public class ApplicationFixture {
// 	public static Application application1() {
// 		Application application = new Application(UserFixture.applicants(), PartyPostFixture.partyPost());
// 		application.setId(775L);
// 		return application;
// 	}
//
// 	public static Application application2() {
// 		Application application = new Application(UserFixture.applicants(), PartyPostFixture.partyPost());
// 		application.setId(775L);
// 		return application;
// 	}
//
// 	public static Page<Application> applications() {
// 		return new PageImpl<>(List.of(application1(), application2()));
// 	}
// }
