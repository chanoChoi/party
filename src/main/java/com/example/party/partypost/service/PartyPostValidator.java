package com.example.party.partypost.service;

import com.example.party.partypost.dto.PartyPostListResponse;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.exception.PartyPostNotDeletableException;
import com.example.party.restriction.entity.Block;
import com.example.party.restriction.repository.BlockRepository;
import com.example.party.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PartyPostValidator {
	private final BlockRepository blockRepository;

	//private 메소드
	//삭제가능여부 확인
	public void canDeletePartyPost(User user, PartyPost partyPost) {
		//1. 작성자인지 확인
		if (!partyPost.isWrittenByMe(user.getId())) {
			throw new PartyPostNotDeletableException("작성자만 모집글을 삭제할 수 있습니다");
		}
		//2. 모집글이 이미 삭제 상태인지 확인
		if (!partyPost.isActive()) {
			throw new PartyPostNotDeletableException("이미 삭제처리된 모집글입니다.");
		}

		//3. 모집마감전인지 확인
		if (!partyPost.beforeCloseDate(LocalDateTime.now())) {
			throw new PartyPostNotDeletableException("모집마감시간이 지나면 모집글을 삭제할 수 없습니다");
		}
		//3. 참가신청한 모집자가 없는지 확인
		if (!partyPost.haveNoApplications()) {
			if (!partyPost.isWrittenByMe(user.getId()))
				throw new PartyPostNotDeletableException("참가신청자가 있는 경우 삭제할 수 없습니다");
		}
	}

	//blockedList 검열
	public List<PartyPostListResponse> filteringPosts(User user, List<PartyPost> partyPostList) {
		List<Block> blockedList = blockRepository.findAllByBlockerId(user.getId());
		Iterator<PartyPost> iterator = partyPostList.iterator();
		List<PartyPost> partyPostList_temp = new ArrayList<>();

		if (blockedList.size() > 0) {
			while (iterator.hasNext()) {
				PartyPost post = iterator.next();
				for (Block block : blockedList) {
					if (post.getUser().getId().equals(block.getBlocked().getId())) {
						partyPostList_temp.add(post);
						iterator.remove();
					}
				}
			}
			partyPostList.removeAll(partyPostList_temp);
		} else {
			return partyPostList.stream().map(PartyPostListResponse::new).collect(Collectors.toList());
		}
		return partyPostList.stream()
				.map(PartyPostListResponse::new).collect(Collectors.toList());
	}
}
