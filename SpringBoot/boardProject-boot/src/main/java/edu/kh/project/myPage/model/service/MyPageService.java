package edu.kh.project.myPage.model.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	int info(Member updateMember, String[] memberAddress);

	int changePw(String currentPw, String newPw, int memberNo);

	int secession(String memberPw, int memberNo);

	int updateProfileImg(MultipartFile profileImage, Member loginMember) throws IllegalStateException, IOException ;

}
