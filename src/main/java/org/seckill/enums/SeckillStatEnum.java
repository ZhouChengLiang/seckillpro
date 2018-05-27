package org.seckill.enums;

/**
 * ʹ��ö�ٱ�����������
 * @author Administrator
 *
 */
public enum SeckillStatEnum {
	SUCCESS(1,"��ɱ�ɹ�"),
	END(0,"��ɱ����"),
	REPEAT_KILL(-1,"�ظ���ɱ"),
	INNER_ERROR(-2,"ϵͳ�쳣"),
	DATA_REWRITE(-3,"���ݴ۸�");
	private Integer state;
	
	private String stateInfo;

	SeckillStatEnum(Integer state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	
	public static SeckillStatEnum stateOf(Integer state){
		for(SeckillStatEnum ss:values()){
			if(ss.getState() ==state){
				return ss;
			}
		}
		return null;
	}
}
