package com.github.aburaagetarou.redivmarche.util.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI用インターフェース
 */
public interface GUI {

	/**
	 * GUI初期化
	 * @param row 行数
	 * @param title GUIタイトル
	 */
	void init(int row, Component title);

	/**
	 * アイテム設定
	 * @param index スロット番号
	 * @param item 表示アイテム
	 */
	void setItem(int index, ItemStack item);

	/**
	 * GUI表示
	 * @param player 表示対象プレイヤー
	 */
	void show(Player player);
}
