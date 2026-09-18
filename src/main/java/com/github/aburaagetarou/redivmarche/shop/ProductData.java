package com.github.aburaagetarou.redivmarche.shop;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.inventory.ItemStack;

/**
 * 商品データ
 */
public class ProductData {

	public String seller;
	public short slot;
	public int price;
	public int amount;
	public String itemId;
	public String data;

	/**
	 * インスタンス生成
	 */
	public ProductData() {
		this.slot = 0;
		this.price = 0;
		this.amount = 0;
	}

	/**
	 * インスタンス生成
	 * @param seller 販売者
	 * @param slot スロット
	 * @param price 価格
	 * @param data データ
	 */
	public ProductData(String seller, short slot, int price, int amount, String itemId, String data) {
		this.seller = seller;
		this.slot = slot;
		this.price = price;
		this.amount = amount;
		this.itemId = itemId;
		this.data = data;
	}

	/**
	 * Skriptのアイテムを商品に変換する
	 * @param seller 販売者
	 * @param slot スロット
	 * @param price 価格
	 * @param item アイテム
	 * @return 商品データ
	 */
	public static ProductData fromSkriptData(String seller, short slot, int price, String itemId, ItemStack item) {

		// データ取得
		int amount = item.getAmount();

		// NBTから変換
		ReadWriteNBT nbt = NBT.itemStackToNBT(item);
		return new ProductData(seller, slot, price, amount, itemId, nbt.toString());
	}
}
