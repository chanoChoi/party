package com.example.party;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.party.application.entity.Application;
import com.example.party.application.repository.ApplicationRepository;
import com.example.party.category.dto.CategoryRequest;
import com.example.party.category.entity.Category;
import com.example.party.category.repository.CategoryRepository;
import com.example.party.partypost.dto.PartyPostRequest;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.restriction.entity.NoShow;
import com.example.party.restriction.repository.NoShowRepository;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.entity.User;
import com.example.party.user.repository.ProfilesRepository;
import com.example.party.user.repository.UserRepository;

@SpringBootApplication
@EnableScheduling
public class PartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartyApplication.class, args);
	}

	@Bean
	public CommandLineRunner dummyData(
		CategoryRepository categoryRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
		ProfilesRepository profilesRepository, PartyPostRepository partyPostRepository,
		NoShowRepository noShowRepository,
		ApplicationRepository applicationRepository
	) {
		return args -> {
			CategoryRequest categoryRequest3 = new CategoryRequest("게임");
			Category category3 = new Category(categoryRequest3);
			categoryRepository.save(category3);

			// 만들 더미 데이터 수를 입력하세요 :
			int createPartyPostSize = 100;
			List<User> users = new ArrayList<>();
			List<User> applicationUsers = new ArrayList<>();
			List<PartyPost> posts = new ArrayList<>();
			Comparator<User> idComparator = Comparator.comparing(User::getId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			// 더미 데이터 찍어내기
			for (int i = 0; i < createPartyPostSize * 5; i++) {
				SignupRequest signupRequest = SignupRequest.builder()
					.email("tester" + i + "@gmail.com")
					.password("asdf!1234")
					.nickname("테스터" + i)
					.build();
				String password = passwordEncoder.encode(signupRequest.getPassword());
				User user = new User(signupRequest, password);
				userRepository.save(user);
				users.add(user);
				if (users.size() == createPartyPostSize * 5) {
					for (int j = 0; j < createPartyPostSize; j++) {
						PartyPostRequest partyPostRequest = PartyPostRequest.builder()
							.title("테스트파티" + j)
							.content("테스트")
							.categoryId((long)3)
							.maxMember((byte)5)
							.partyDate("2023-03-09 16:00")
							.partyAddress("onLine")
							.partyPlace("onLine")
							.build();
						LocalDateTime partyDate = LocalDateTime.parse(partyPostRequest.getPartyDate(), formatter);
						User partyLeader = users.get(j);
						PartyPost partyPost = new PartyPost(partyLeader, partyPostRequest, partyDate, category3);
						partyPost.changeStatusNoShow();
						partyPostRepository.save(partyPost);
						posts.add(partyPost);
						// 파티장 추가
						Application LeaderApplication = new Application(partyLeader, partyPost);
						LeaderApplication.accept();
						applicationRepository.save(LeaderApplication);
						users.remove(partyLeader);
					}
				}
			}

			// Long 10 부터 아스키 코드로 뱉어서 문제가 발생함
			System.out.println("\n");
			for (User user : users) {
				System.out.println(user);
			}
			System.out.println("\n");

			if (posts.size() == createPartyPostSize) {
				for (int k = 0; k < createPartyPostSize; k++) {
					PartyPost post = posts.get(k);
					for (int l = 0; l < post.getMaxMember() - 1; l++) {
						User userApplication = users.get(l);

						Application application = new Application(userApplication, post);
						application.accept();
						applicationRepository.save(application);
						// users.remove(userApplication);
						applicationUsers.add(userApplication);
					}
					if (applicationUsers.size() == post.getMaxMember() - 1) {
						// for문 도는 와중에 users.remove() 를 하니 즉 액세스 하는 동안 목록이 수정되어 예기치 않은 동작, 오류가 발생했음
						users.removeAll(applicationUsers);
						applicationUsers.add(post.getUser());
						applicationUsers.sort(idComparator);
					}

					System.out.println("\n" + applicationUsers + "\n");

					if (applicationUsers.size() == post.getMaxMember()) {
						User ReportUser = applicationUsers.get(1);
						applicationUsers.remove(ReportUser);

						for (int m = 0; m < applicationUsers.size(); m++) {
							NoShow noShow = new NoShow(applicationUsers.get(m).getId(),
								ReportUser.getId(), post.getId());
							noShowRepository.save(noShow);
						}
						applicationUsers.clear();
					}
				}
			}
		};
	}
}
