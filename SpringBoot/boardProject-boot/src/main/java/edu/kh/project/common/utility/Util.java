package edu.kh.project.common.utility;

import java.text.SimpleDateFormat;

public class Util {

	public static int seqNum = 1;
	
	
	/** 파일명 변경 메소드(날짜+seqNum.확장자)
	 * @param originFileName
	 * @return
	 */
	public static String fileRename(String originFileName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sdf.format(new java.util.Date(System.currentTimeMillis()));

		String str = "_" + String.format("%05d", seqNum++);

		String ext = originFileName.substring(originFileName.lastIndexOf("."));

		return date + str + ext;
	}
	
	
	

	// Cross Site Scripting(XSS) 방지 처리
	// - 웹 애플리케이션에서 발생하는 취약점
	// - 권한이 없는 사용자가 사이트에 스크립트를 작성하는 것
	public static String XSSHandling(String content) {
		
		// 스크립트나 마크업 언어에서 기호나 기능을 나타내는 문자를 변경 처리
		
		//   &  - &amp;
		//   <  - &lt;
		//   >  - &gt;
		//   "  - &quot;
		
		content = content.replaceAll("&", "&amp;");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll("\"", "&quot;");
		
		return content;
	}
}
