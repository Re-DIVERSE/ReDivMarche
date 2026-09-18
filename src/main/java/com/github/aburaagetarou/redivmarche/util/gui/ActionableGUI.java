package com.github.aburaagetarou.redivmarche.util.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * GUI実装クラス
 */
public class ActionableGUI implements InventoryHolder, GUI, Listener {

	private Inventory inventory;
	private Player owner;
	private final Map<Integer, Consumer<GUIAction>> actionMap;

	/**
	 * インスタンス生成
	 */
	public ActionableGUI() {
		actionMap = new HashMap<>();
	}

	/**
	 * オーバーライド
	 * @return インベントリ
	 */
	@NotNull
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * GUI初期化
	 * @param row 行数
	 * @param title GUIタイトル
	 */
	@Override
	public void init(int row, Component title) {

		// インベントリ作成
		inventory = Bukkit.createInventory(this, (row * 9), title);
	}

	/**
	 * 表示アイテムをセット
	 * @param index スロット番号
	 * @param item 表示アイテム
	 */
	@Override
	public void setItem(int index, ItemStack item) {
		setButton(index, item, null);
	}

	/**
	 * ボタン作成
	 * @param index スロット番号
	 * @param item 表示アイテム
	 * @param action 動作
	 */
	public void setButton(int index, ItemStack item, Consumer<GUIAction> action) {
		if(inventory == null) return;

		// アイテムをセット
		inventory.setItem(index, item);

		// 動作を保存
		if(action != null) {
			actionMap.put(index, action);
		}
		else {
			actionMap.remove(index);
		}
	}

	/**
	 * 表示
	 * @param player 表示対象プレイヤー
	 */
	@Override
	public void show(Player player) {
		player.openInventory(inventory);
		owner = player;
	}

	/**
	 * 対象スロットが動作を持つか判定
	 * @param slot スロット
	 * @return 動作有無
	 */
	public boolean slotHasAction(int slot) {
		return actionMap.containsKey(slot);
	}

	/**
	 * 対象スロットの動作を発火
	 * @param slot スロット
	 */
	public void runAction(int slot) {

		// 動作がない場合は処理しない
		if(!slotHasAction(slot)) return;

		// アイテムを取得
		ItemStack item = inventory.getItem(slot);
		if(item == null) return;

		// 発火
		GUIAction data = new GUIAction(owner, slot, item);
		actionMap.get(slot).accept(data);
	}
}
