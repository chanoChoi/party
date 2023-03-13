package com.example.party.partypost.service;

import com.example.party.application.entity.Application;
import com.example.party.application.repository.ApplicationRepository;
import com.example.party.category.entity.Category;
import com.example.party.category.exception.CategoryNotActiveException;
import com.example.party.category.exception.CategoryNotFoundException;
import com.example.party.category.repository.CategoryRepository;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.common.ItemApiResponse;
import com.example.party.global.exception.NotFoundException;
import com.example.party.partypost.dto.*;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.exception.IsNotWritterException;
import com.example.party.partypost.exception.PartyPostNotFoundException;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PartyPostService implements IPartyPostService {

    private final PartyPostRepository partyPostRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final CategoryRepository categoryRepository;
    private final PartyPostValidator partyPostValidator;

    //모집글 작성
    @Override
    public ApiResponse createPartyPost(User user, PartyPostRequest request) { // 인자 달라질 수 있습니다
        User user1 = userRepository.findById(user.getId()).orElseThrow(NotFoundException::new);

        //예시: "2023-02-16 12:00"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime partyDate = LocalDateTime.parse(request.getPartyDate(), formatter);
        //1. category 찾기
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);
        //2. category 활성화 상태 확인
        if (!category.isActive()) { throw new CategoryNotActiveException();}

        //3. PartyPost 객체 생성
        PartyPost partyPost = new PartyPost(user, request, partyDate, category);
        PartyPost partyPost1 = partyPostRepository.save(partyPost);

        // 모집글이 작성될 때 파티장을 Application 에 추가
        Application application = new Application(user1, partyPost1);
        application.accept();
        applicationRepository.save(application);

        partyPost1.addApplication(application);
        user1.addApplication(application);

        return ApiResponse.create("모집글 작성 완료");
    }

    //모집글 전체 조회
    @Override
    @Transactional
    public DataApiResponse<PartyPostListResponse> findPartyList(User user, int page) {
        // 1.모집글 전체 불러오기 (페이지 추가)
        Pageable pageable = PageRequest.of(page, 6, Sort.by("createdAt").descending());
        //2. Page<partyPost> 를 Page<PartyPostListResponse> 로 변경
        List<PartyPost> pageResponse = partyPostRepository.findAllByActiveIsTrue(pageable);
        //blockedList 를 체크해서 걸러줌. 걸러진 페이지에 대한 처리는 아직 없음
        List<PartyPostListResponse> filteredPosts = partyPostValidator.filteringPosts(user, pageResponse);
        return DataApiResponse.ok("모집글 전체 조회 완료", filteredPosts);
    }

    //모집글 상세 조회(개별 상세조회)
    @Override
    @Transactional
    public ItemApiResponse<PartyPostResponse> getPartyPost(Long partyPostId, User user) {
        //1. partyPostId의 partyPost 를 가져오기
        PartyPost partyPost = partyPostRepository.findByIdAndActiveIsTrue(partyPostId)
                .orElseThrow(PartyPostNotFoundException::new);

        //2. 조회자!=작성자 인경우 조회한 partyPost 의 조회수 올려주기
        partyPost.increaseViewCnt(user);

        //2. PartyPostResponse Dto 생성
        PartyPostResponse partyPostResponse = new PartyPostResponse(partyPost);

        //3. DataResponseDto 생성하고 (2) 를 넣어준 후 return
        return ItemApiResponse.ok("모집글 상세 조회 완료", partyPostResponse);
    }

    //문자 검색으로 제목,지역명으로 모집글 조회
    @Override
    @Transactional
    public DataApiResponse<PartyPostListResponse> searchPartyPost(User user, String searchText, int page) {
        Pageable pageable = PageRequest.of(page - 1, 6 , Sort.by("createdAt").descending());

        //1.검색 문자에 맞는 리스트 조회
        List<PartyPost> partyPostList = partyPostRepository.findByTitleContainingOrAddressContaining(searchText,
                searchText,
                pageable);
        // 유저의 차단 리스트를 필터링 ,유저가 로그인 상태일때에 적용
        List<PartyPostListResponse> filteredPosts = userFilteredPosts(user,partyPostList);

        return DataApiResponse.ok("모집글 검색 완료", filteredPosts);
    }

    //카테고리 별 모집글 조회
    @Override
    public DataApiResponse<PartyPostListResponse> searchPartyPostByCategory(User user, Long categoryId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 6 , Sort.by("createdAt").descending());

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                CategoryNotFoundException::new
        );

        if (!category.isActive()) {
            throw new CategoryNotActiveException();
        }

        List<PartyPost> partyPostList = partyPostRepository.findByCategoryIdAndActiveIsTrue(categoryId, pageable);

        // 유저의 차단 리스트를 필터링 ,유저가 로그인 상태일때에 적용
        List<PartyPostListResponse> filteredPosts = userFilteredPosts(user,partyPostList);

        return DataApiResponse.ok("카테고리별 모집글 조회 완료", filteredPosts);
    }

    //핫한 모집글 조회 (조회수)
    @Override
    public DataApiResponse<PartyPostListResponse> findHotPartyPost(User user) {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("ViewCnt"));

        List<PartyPost> partyPostList = partyPostRepository.findFirst3ByOrderByViewCntDesc(pageable);

        // 유저의 차단 리스트를 필터링 ,유저가 로그인 상태일때에 적용
        List<PartyPostListResponse> filteredPosts = userFilteredPosts(user, partyPostList);

        return DataApiResponse.ok("핫한 모집글 조회 완료", filteredPosts);
    }


    //내주변 모집글 조회
    @Override
    public DataApiResponse<PartyPostListResponse> findNearPartyPost(User user, String string) {
        Pageable pageable = PageRequest.of(0, 3); //페이지 갯수 지정

        String[] list = string.split(" ");
        String searchAddress = list[2];

        //1.검색 문자에 맞는 리스트 조회
        List<PartyPost> partyPostList = partyPostRepository.findByAddressContaining(searchAddress, pageable);
        //2. 유저의 차단 리스트를 필터링 ,유저가 로그인 상태일때에 적용
        List<PartyPostListResponse> filteredPosts = userFilteredPosts(user,partyPostList);

        return DataApiResponse.ok("주변 모집글 조회 완료", filteredPosts);
    }

    //모집글 수정
    @Override
    public ApiResponse updatePartyPost(Long partyPostId,
                                       UpdatePartyPostRequest request, User user) {

        //1. PartyPost 불러오기
        PartyPost partyPost = partyPostRepository.findById(partyPostId).orElseThrow(
                PartyPostNotFoundException::new
        );

        //2. Category 불러오기
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                CategoryNotFoundException::new
        );

        //3. 작성자인지 확인
        if (!partyPost.isWrittenByMe(user.getId())) {
            throw new IsNotWritterException();
        }

        //4. 수정할 내용 받기
        partyPost.update(request, category);

        //5. DataResponseDto 생성 후 return
        return ApiResponse.ok("모집글 수정 완료");
    }

    //모집글 삭제
    @Override
    public ApiResponse deletePartyPost(Long partyPostId, User user) {
        //1. partyPost 객체 가져오기
        PartyPost partyPost = partyPostRepository.findById(partyPostId).orElseThrow(
                PartyPostNotFoundException::new
        );
        //2. 삭제 가능한지 확인 (작성자인지 / 모집마감전인지 / 참가신청한 모집자가 없는지)
        partyPostValidator.canDeletePartyPost(user, partyPost);

        //3. 2가 통과한 경우 삭제 진행
        partyPost.deletePartyPost();
        return ApiResponse.ok("모집글 삭제 완료");
    }

    //내가 작성한 모집글 조회 ( 내가 파티장인 경우만 )
    @Override
    public DataApiResponse<MyPartyPostListResponse> findMyCreatedPartyList(User user, int page) {
        Pageable pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.DESC, "modifiedAt"));

        //1. user가 작성한 partyPost의 리스트
        List<PartyPost> myPartyPostList = partyPostRepository.findByUserIdAndActiveIsTrue(user.getId(), pageable);

        //2. partyPost DTO의 LIST 생성
        List<MyPartyPostListResponse> myPartyPostDtoList = myPartyPostList.stream()
                .filter(partyPost -> partyPost.getUser().getId().equals(user.getId()))
                .map(partyPost -> new MyPartyPostListResponse(partyPost,user.getId())).collect(Collectors.toList());

        //3. DataResponseDto 생성 후 return
        return DataApiResponse.ok("내가 작성한 모집글 조회 완료", myPartyPostDtoList);
    }

    //내가 신청한 모집글 조회 ( 내가 파티원인 경우만/ 파티장인 경우 제외 )
    @Override
    public DataApiResponse<MyPartyPostListResponse> findMyJoinedPartyList(User user, int page) {
        Pageable pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.DESC, "modifiedAt"));

        //1. user가 신청한 application의 리스트
        List<Application> myApplicationList = applicationRepository.findByUserId(user.getId(), pageable);

        //2. partyPost DTO의 LIST 생성
        List<MyPartyPostListResponse> myApplicationDtoList = myApplicationList.stream()
                .map(Application::getPartyPost)
                .filter(partyPost -> !partyPost.getUser().getId().equals(user.getId()))
                .map(partyPost -> new MyPartyPostListResponse(partyPost, user.getId())).collect(Collectors.toList());

        //3. DataResponseDto 생성 후 return
        return DataApiResponse.ok("내가 참가한 모집글 조회 완료", myApplicationDtoList);
    }

    //모집게시물 좋아요 (*좋아요 취소도 포함되는 기능임)
    public ApiResponse toggleLikePartyPost(Long partyPostId, User user) {
        //모집글 찾기
        PartyPost partyPost = partyPostRepository.findById(partyPostId).orElseThrow(
                PartyPostNotFoundException::new
        );
        User userT = userRepository.save(user);
        //좋아요 확인
        if (!(userT.getLikePartyPosts().add(partyPost))) {
            userT.getLikePartyPosts().remove(partyPost);
            return ApiResponse.ok("모집글 좋아요 취소 완료");
        } else {
            return ApiResponse.ok("모집글 좋아요 완료");
        }
    }

    //좋아요 누른 게시물 조회
    //Repository 에 저장된 값을 가져오는게 아닐 때 pageable 적용법을 몰라 일단 패스함
    public DataApiResponse<PartyPostListResponse> getLikePartyPost(User user) {
        User userIf = userRepository.save(user);
        if (userIf.getLikePartyPosts().isEmpty()) {
            throw new NotFoundException();
        }
        List<PartyPostListResponse> partyPost = userIf.getLikePartyPosts().stream()
                .map(PartyPostListResponse::new).collect(Collectors.toList());
        return DataApiResponse.ok("좋아요 게시글 조회 완료", partyPost);
    }

    //유저의 차단 리스트를 필터링 ,유저가 로그인 상태일때에 적용
    public List<PartyPostListResponse> userFilteredPosts(User user,List<PartyPost> partyPostList)
    {
        List<PartyPostListResponse> filteredPosts;
        if (user==null) {
            filteredPosts = partyPostList.stream().map(PartyPostListResponse::new).collect(Collectors.toList());
        } else {
            filteredPosts = partyPostValidator.filteringPosts(user, partyPostList);
        }
        return filteredPosts;
    }

}
