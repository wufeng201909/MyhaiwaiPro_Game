package com.sdk.mysdklibrary.localbeans;



public class GameRoleBean {
	public static final int CREATE_ROLE = 0;
	public static final int ENTER_GAME = 1;
	public static final int ROLE_LEVEL_UP = 2;

	//和游戏角色相关的
	private String gameZoneId;       //游戏区服ID
	private String gameZoneName;       //游戏区服ID
	private String roleId;           //角色ID
	private String roleName;         //角色名称
	private int roleLevel = 0;            //角色等级
	private String RoleCTime;   //创角时间
    private int vipLevel = -1;   //VIP等级

	//和支付相关的
	private String gameOrderId;               //游戏订单ID
	private int gameCoin;         //游戏币
	private String productId;    //商品id
	private String gameExt;

	public String getGameZoneName() {
		return gameZoneName;
	}

	public void setGameZoneName(String gameZoneName) {
		this.gameZoneName = gameZoneName;
	}

	public static int getCreateRole() {
		return CREATE_ROLE;
	}

	public static int getEnterGame() {
		return ENTER_GAME;
	}

	public static int getRoleLevelUp() {
		return ROLE_LEVEL_UP;
	}

	public String getGameZoneId() {
		return gameZoneId;
	}

	public void setGameZoneId(String gameZoneId) {
		this.gameZoneId = gameZoneId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}

	public String getRoleCTime() {
		return RoleCTime;
	}

	public void setRoleCTime(String RoleCTime) {
		this.RoleCTime = RoleCTime;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public String getGameOrderId() {
		return gameOrderId;
	}

	public void setGameOrderId(String gameOrderId) {
		this.gameOrderId = gameOrderId;
	}

	public int getGameCoin() {
		return gameCoin;
	}

	public void setGameCoin(int gameCoin) {
		this.gameCoin = gameCoin;
	}

	public String getGameExt() {
		return gameExt;
	}

	public void setGameExt(String gameExt) {
		this.gameExt = gameExt;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * 创角，登录，等级点 游戏参数检查上报
	 * @return
	 */
	public void submitRoleInfoCheck() {
	}

}
