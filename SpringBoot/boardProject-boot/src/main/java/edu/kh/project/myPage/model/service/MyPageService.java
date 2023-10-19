package edu.kh.project.myPage.model.service;

import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	int info(Member updateMember, String[] memberAddress);

	int changePw(String currentPw, String newPw, int memberNo);

	int secession(String memberPw, int memberNo);

}
