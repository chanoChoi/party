package com.example.party.restriction.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.party.application.entity.Application;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.exception.BadRequestException;
import com.example.party.global.exception.NotFoundException;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.exception.PartyPostNotFoundException;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.partypost.type.Status;
import com.example.party.restriction.dto.BlockResponse;
import com.example.party.restriction.dto.NoShowRequest;
import com.example.party.restriction.dto.ReportPostRequest;
import com.example.party.restriction.dto.ReportUserRequest;
import com.example.party.restriction.entity.Block;
import com.example.party.restriction.entity.NoShow;
import com.example.party.restriction.entity.ReportPost;
import com.example.party.restriction.entity.ReportUser;
import com.example.party.restriction.repository.BlockRepository;
import com.example.party.restriction.repository.NoShowRepository;
import com.example.party.restriction.repository.ReportPostRepository;
import com.example.party.restriction.repository.ReportUserRepository;
import com.example.party.user.entity.User;
import com.example.party.user.exception.UserNotFoundException;
import com.example.party.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RestrictionService {
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final NoShowRepository noShowRepository;
    private final ReportUserRepository reportUserRepository;
    private final ReportPostRepository reportPostRepository;
    private final PartyPostRepository partyPostRepository;

    public ApiResponse blockUser(User user, Long userId) {
        User blocked = findByUser(userId);
        isMySelf(user, userId);
        List<Block> blocks = getBlocks(user.getId());

        if (blocks.stream().anyMatch(b -> b.getBlocked().equals(blocked))) {
            throw new BadRequestException("?????? ????????? ???????????????");
        }

        Block block = new Block(user, blocked);
        blockRepository.save(block);
        return ApiResponse.ok("???????????? ??????");
    }

    //????????????
    public ApiResponse unBlockUser(User user, Long userId) {
        User blocked = findByUser(userId);
        isMySelf(user, userId);
        List<Block> blocks = getBlocks(user.getId());
        Optional<Block> blockdate = blocks.stream()
                .filter(b -> b.getBlocked().equals(blocked)).findFirst();

        if (blockdate.isPresent()) {
            blockRepository.delete(blockdate.get());
        } else {
            throw new BadRequestException("????????? ????????? ????????????");
        }

        return ApiResponse.ok("???????????? ??????");
    }

    //???????????? ??????
    public DataApiResponse<BlockResponse> getBlockedList(int page, User user) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Block> blocks = blockRepository.findAllByBlockerId(user.getId(), pageable);

        if (blocks.size() == 0) {
            throw new BadRequestException("????????? ????????? ????????????");
        }

        List<BlockResponse> blockResponse = blocks.stream().map(BlockResponse::new).collect(Collectors.toList());
        return DataApiResponse.ok("?????? ??????", blockResponse);
    }

    //?????? ??????
    public ApiResponse createReportUser(User user, ReportUserRequest request) {
        //????????? ??????
        isMySelf(user, request.getUserId());
        User reported = findByUser(request.getUserId());
        // ?????? ????????? ????????? ????????? ??????
        checkExistingData(user.getId(), reported.getId(), null);
        ReportUser reportUser = new ReportUser(user, reported, request);
        reportUserRepository.save(reportUser);
        return ApiResponse.ok("?????? ?????? ??????");
    }

    //????????? ??????
    public ApiResponse createReportPost(User user, ReportPostRequest request) {
        PartyPost partyPost = partyPostRepository.findByIdAndActiveIsTrue(request.getPostId())
                .orElseThrow(PartyPostNotFoundException::new);
        isMySelf(user, partyPost.getUser().getId());
        // ?????? ????????? ????????? ????????? ??????
        checkExistingData(user.getId(), null, partyPost.getId());
        ReportPost reportsPost = new ReportPost(user, request, partyPost);
        reportPostRepository.save(reportsPost);
        return ApiResponse.ok("????????? ?????? ??????");
    }

    //?????? ??????
    public ApiResponse reportNoShow(User user, NoShowRequest request) {
        isMySelf(user, request.getUserId());
        //????????? ??????
        User reported = findByUser(request.getUserId());
        PartyPost partyPost = getPartyPost(request.getPartyPostId());

        if (!partyPost.getStatus().equals(Status.NO_SHOW_REPORTING)) {
            throw new BadRequestException("?????? ?????? ????????? ?????????????????????");
        }

        List<Application> applicationList = partyPost.getApplications();
        // ?????? ???????????? ????????? ??????, ???????????? ????????? ????????? ??????
        if (applicationList.stream().noneMatch(a -> a.getUser().equals(reported)) ||
                applicationList.stream().noneMatch(a -> a.getUser().equals(user))) {
            throw new BadRequestException("Not a party member");
        }

        // ?????? ????????? ????????? ????????? ??????
        checkExistingData(user.getId(), reported.getId(), partyPost.getId());
        NoShow noShow = new NoShow(user.getId(), reported.getId(), partyPost.getId());
        noShowRepository.save(noShow);
        return ApiResponse.ok("?????? ?????? ??????");
    }

    //status.END ?????? ???????????? ?????? ?????? ?????? ??????
    @Transactional
    public void checkingNoShow(List<PartyPost> posts) {
        for (PartyPost partyPost : posts) {
            partyPost.ChangeStatusEnd();
            int joinUserSize = partyPost.getApplications().size();
            List<NoShow> noShowList = noShowRepository.findAllByPartyPostId(partyPost.getId());
			// reportId ?????? NoShow ????????? ??????????????????.
            Map<Long, List<NoShow>> reportedNoShowMap = new HashMap<>();
            for (NoShow noShow : noShowList) {
                Long reportedId = noShow.getReportedId();
				// key ??? ????????? ????????? ???????????? ????????? ?????? ????????? ?????? ArrayList??? ????????? noShow ??? ??????
                reportedNoShowMap.computeIfAbsent(reportedId, k -> new ArrayList<>()).add(noShow);
            }
            // Map.Entry : ????????? ???????????? key, Value ?????? ????????? ??? ??????
            for (Map.Entry<Long, List<NoShow>> entry : reportedNoShowMap.entrySet()) {
                Long reportedId = entry.getKey();
                List<NoShow> reportedNoShowList = entry.getValue();
                int noShowReportCnt = reportedNoShowList.size();

                if (noShowReportCnt >= Math.round(joinUserSize / 2.0)) {
                    User reported = findByUser(reportedId);
                    reported.getProfile().plusNoShowCnt();
                }
            }
        }
    }

    //private
    private User findByUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private List<Block> getBlocks(Long userId) {
        return blockRepository.findAllByBlockerId(userId);
    }

    private PartyPost getPartyPost(Long postId) {
        return partyPostRepository.findById(postId)
                .orElseThrow(NotFoundException::new);
    }

    private void isMySelf(User users, Long targetId) {
        if (users.getId().equals(targetId))
            throw new BadRequestException("?????? ??????????????????");
    }

    // DB??? ???????????? ????????? ??????????????? ??????
    private void checkExistingData(Long userId, Long reportedId, Long partyPostId) {
        boolean userIsNull = userId == null;
        boolean targetIsNull = reportedId == null;
        boolean postIsNull = partyPostId == null;

        if (!userIsNull) {
            if (!targetIsNull && !postIsNull) {
                if (noShowRepository.existsByReporterIdAndReportedIdAndPartyPostId(userId, reportedId,
                        partyPostId)) {
                    throw new BadRequestException("?????? ????????? ???????????????");
                }
            }
            if (targetIsNull) {
                if (reportPostRepository.existsByUserIdAndPartyPostId(userId, partyPostId)) {
                    throw new BadRequestException("?????? ????????? ??????????????????");
                }
            }
            if (postIsNull) {
                if (reportUserRepository.existsByReporterIdAndReportedId(userId, reportedId)) {
                    throw new BadRequestException("?????? ?????? ????????? ???????????????");
                }
            }
        } else {
            throw new BadRequestException("????????? ???????????????");
        }
    }
}

