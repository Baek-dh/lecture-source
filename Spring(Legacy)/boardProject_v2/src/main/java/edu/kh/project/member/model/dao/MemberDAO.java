package edu.kh.project.member.model.dao;

import edu.kh.project.member.model.dto.Member;

public interface MemberDAO {
	/** 로그인 DAO
	 * @param inputMember
	 * @return 회원 정보 또는 null
	 */
	Member login(Member inputMember);
		
	/** 회원 가입 DAO
	 * @param inputMember
	 * @return result
	 */
	public int signUp(Member inputMember);
}	
