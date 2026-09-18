package com.github.aburaagetarou.redivmarche.util.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GUIListener implements Listener {

	/**
	 * GUIクリック時処理
	 * @param event クリックイベントクラス
	 */
	@EventHandler
	public void onGUIClick(InventoryClickEvent event) {

		// このクラスのインベントリかどうかチェック
		Inventory inv = event.getInventory();
		if(!(inv.getHolder() instanceof ActionableGUI)) return;

		// このクラスのインベントリにキャスト
		ActionableGUI gui = (ActionableGUI)inv.getHolder();

		// イベントをキャンセル
		event.setCancelled(true);

		// アイテムと動作が存在する場合
		int slot = event.getSlot();

		// 動作を発火
		gui.runAction(slot);
	}
}
