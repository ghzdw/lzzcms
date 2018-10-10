package com.lzzcms.model;
/**
 * 自动生成关键字、摘要时使用
 * @author zhao
 *
 */
public class OneTerm implements Comparable<OneTerm>{
	private Long freq;//term出现的次数
	private String term;//一个域分词后的关键字
	public Long getFreq() {
		return freq;
	}
	public void setFreq(Long freq) {
		this.freq = freq;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	@Override
	public int compareTo(OneTerm oneTerm) {
		return new Long(oneTerm.freq-freq).intValue();
	}
	public OneTerm(Long freq, String term) {
		this.freq = freq;
		this.term = term;
	}
	
}
