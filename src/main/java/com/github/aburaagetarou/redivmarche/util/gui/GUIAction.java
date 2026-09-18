package com.github.aburaagetarou.redivmarche.util.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUIボタン押下後動作
 */
public class GUIAction {

	Player player;
	int index;
	ItemStack item;

	/**
	 * インスタンス
	 * @param player クリック者
	 * @param index スロット番号
	 * @param item ボタンアイテム
	 */
	public GUIAction(Player player, int index, ItemStack item) {
		this.player = player;
		this.index = index;
		this.item = item;
	}

	/**
	 * クリック者を得る
	 * @return クリック者
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * スロット番号を得る
	 * @return スロット番号
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * ボタンアイテムを得る
	 * @return ボタンアイテム
	 */
	public ItemStack getItem() {
		return item;
	}
}
