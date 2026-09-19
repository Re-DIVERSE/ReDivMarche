package com.github.aburaagetarou.redivmarche.shop;

import com.github.aburaagetarou.redivmarche.ReDivMarche;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ショップデータ管理
 */
public class ShopDataManager {

	// ログタイプ
	public static final short LOG_TYPE_BOUGHT   = 1;	// 購入
	public static final short LOG_TYPE_WITHDRAW = 2;	// 取り下げ

	/**
	 * テーブル作成
	 */
	public static void createTable() throws SQLException {

		// オープン
		Connection con = ReDivMarche.dataSource.getConnection();

		// SQL文作成
		StringBuilder sql = new StringBuilder();
		sql.append(" CREATE TABLE IF NOT EXISTS DiverseUserShop ( ");
		sql.append("   seller      varchar(40)   NOT NULL        ");
		sql.append(" , slot        smallint      NOT NULL        ");
		sql.append(" , price       int           NOT NULL        ");
		sql.append(" , amount      int           NOT NULL        ");
		sql.append(" , itemId      varchar(50)   NOT NULL        ");
		sql.append(" , data        text                          ");
		sql.append(" , PRIMARY KEY(seller, slot)                 ");
		sql.append(" )                                           ");
		PreparedStatement stmt = con.prepareStatement(sql.toString());

		// SQL文実行
		stmt.executeUpdate();

		// クローズ
		stmt.close();

		// SQL文作成
		sql = new StringBuilder();
		sql.append(" CREATE TABLE IF NOT EXISTS DiverseUserShopLog ( ");
		sql.append("   seller      varchar(40)   NOT NULL           ");
		sql.append(" , slot        smallint      NOT NULL           ");
		sql.append(" , price       int           NOT NULL           ");
		sql.append(" , amount      int           NOT NULL           ");
		sql.append(" , itemId      varchar(50)   NOT NULL           ");
		sql.append(" , data        text                             ");
		sql.append(" , proc_type   smallint                         ");
		sql.append(" , proc_time   date           NOT NULL          ");
		sql.append(" )                                              ");
		PreparedStatement stmtLog = con.prepareStatement(sql.toString());

		// SQL文実行
		stmtLog.executeUpdate();

		// クローズ
		stmtLog.close();
		con.close();
	}

	/**
	 * アイテムデータを読み込み
	 */
	public static List<ProductData> loadProducts(int from, int to) {

		try(Connection con = ReDivMarche.dataSource.getConnection()) {

			// SQL文作成
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT *                      ");
			sql.append(" FROM (                        ");
			sql.append("   SELECT seller               ");
			sql.append("        , slot                 ");
			sql.append("        , price                ");
			sql.append("        , amount               ");
			sql.append("        , itemId               ");
			sql.append("        , data                 ");
			sql.append("        , ROW_NUMBER() OVER( ORDER BY price, slot, seller ) AS row_nmb");
			sql.append("   FROM   DiverseUserShop      ");
			sql.append(" ) Work_Data                   ");
			sql.append(" WHERE  Work_Data.row_nmb >= ? ");
			sql.append(" AND    Work_Data.row_nmb <= ? ");
			sql.append(" ORDER BY Work_Data.price      ");
			sql.append("        , Work_Data.slot       ");
			sql.append("        , Work_Data.seller     ");
			PreparedStatement stmt = con.prepareStatement(sql.toString());

			// 条件セット
			stmt.setInt(1, from);
			stmt.setInt(2, to);

			// SQL文実行
			ResultSet result = stmt.executeQuery();

			// データ読み込み
			List<ProductData> products = new ArrayList<>();
			while(result.next()) {
				ProductData product = new ProductData();
				product.seller = result.getString("seller");
				product.slot = result.getShort("slot");
				product.price = result.getInt("price");
				product.amount = result.getInt("amount");
				product.itemId = result.getString("itemId");
				product.data = result.getString("data");

				// 商品データ追加
				products.add(product);
			}

			// クローズ
			result.close();
			stmt.close();

			return products;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 特定のプレイヤーの商品を読み込み
	 * @param seller プレイヤーのUUID
	 */
	public static List<ProductData> loadSellerProducts(String seller) {

		try(Connection con = ReDivMarche.dataSource.getConnection()) {

			// SQL文作成
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT slot            ");
			sql.append("      , price           ");
			sql.append("      , amount          ");
			sql.append("      , itemId          ");
			sql.append("      , data            ");
			sql.append(" FROM   DiverseUserShop ");
			sql.append(" WHERE  seller = ?      ");
			sql.append(" ORDER BY slot          ");
			PreparedStatement stmt = con.prepareStatement(sql.toString());

			// 条件セット
			stmt.setString(1, seller);

			// SQL文実行
			ResultSet result = stmt.executeQuery();

			// データ読み込み
			List<ProductData> ret = new ArrayList<>();
			while(result.next()) {
				ProductData product = new ProductData();
				product.seller = seller;
				product.slot = result.getShort("slot");
				product.price = result.getInt("price");
				product.data = result.getString("data");
				product.amount = result.getInt("amount");
				product.itemId = result.getString("itemId");

				// 商品データ追加
				ret.add(product);
			}

			// クローズ
			result.close();
			stmt.close();

			return ret;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 特定の商品を読み込み
	 * @param seller プレイヤーのUUID
	 * @param slot スロット
	 */
	public static ProductData loadProduct(String seller, short slot) {

		try(Connection con = ReDivMarche.dataSource.getConnection()) {

			// SQL文作成
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT price           ");
			sql.append("      , amount          ");
			sql.append("      , itemId          ");
			sql.append("      , data            ");
			sql.append(" FROM   DiverseUserShop ");
			sql.append(" WHERE  seller = ?      ");
			sql.append(" AND    slot   = ?      ");
			PreparedStatement stmt = con.prepareStatement(sql.toString());

			// 条件セット
			stmt.setString(1, seller);
			stmt.setInt(2, slot);

			// SQL文実行
			ResultSet result = stmt.executeQuery();

			// データ読み込み
			ProductData ret = new ProductData();
			if(result.next()) {
				ret.seller = seller;
				ret.slot = slot;
				ret.price = result.getInt("price");
				ret.amount = result.getInt("amount");
				ret.itemId = result.getString("itemId");
				ret.data = result.getString("data");
			}

			// クローズ
			result.close();
			stmt.close();

			return ret;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 商品を追加
	 * @param product 商品
	 */
	public static boolean addProduct(ProductData product) {

		try(Connection con = ReDivMarche.dataSource.getConnection()) {

			// SQL文作成
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO DiverseUserShop ( ");
			sql.append("        seller                 ");
			sql.append("      , slot                   ");
			sql.append("      , price                  ");
			sql.append("      , amount                 ");
			sql.append("      , itemId                 ");
			sql.append("      , data                   ");
			sql.append(" ) VALUES (                    ");
			sql.append("        ?                      ");
			sql.append("      , ?                      ");
			sql.append("      , ?                      ");
			sql.append("      , ?                      ");
			sql.append("      , ?                      ");
			sql.append("      , ?                      ");
			sql.append(" )                             ");
			PreparedStatement stmt = con.prepareStatement(sql.toString());

			// 条件セット
			stmt.setString(1, product.seller);
			stmt.setShort(2, product.slot);
			stmt.setInt(3, product.price);
			stmt.setInt(4, product.amount);
			stmt.setString(5, product.itemId);
			stmt.setString(6, product.data);

			// SQL文実行
			int count = stmt.executeUpdate();

			// クローズ
			stmt.close();
			return count == 1;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 商品を削除
	 * @param seller プレイヤーのUUID
	 * @param slot スロット
	 */
	public static boolean removeProduct(String seller, short slot, short logType) {

		try(Connection con = ReDivMarche.dataSource.getConnection()) {

			// 商品を読み込み
			ProductData product = loadProduct(seller, slot);
			if(product == null) return false;

			// SQL文作成
			StringBuilder sql = new StringBuilder();
			sql.append(" DELETE FROM DiverseUserShop ");
			sql.append(" WHERE  seller = ?           ");
			sql.append(" AND    slot   = ?           ");
			PreparedStatement stmt = con.prepareStatement(sql.toString());

			// 条件セット
			stmt.setString(1, seller);
			stmt.setInt(2, slot);

			// SQL文実行
			int count = stmt.executeUpdate();

			// ロールバック・コミット
			// ロギング
			writeLog(con, product, logType);

			// クローズ
			stmt.close();
			return count == 1;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ログを保存
	 * @param product 商品
	 */
	private static boolean writeLog(Connection con, ProductData product, short logType) throws SQLException {

		// SQL文作成
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO DiverseUserShopLog ( ");
		sql.append("        seller                    ");
		sql.append("      , slot                      ");
		sql.append("      , price                     ");
		sql.append("      , amount                    ");
		sql.append("      , itemId                    ");
		sql.append("      , data                      ");
		sql.append("      , proc_type                 ");
		sql.append("      , proc_time                 ");
		sql.append(" ) VALUES (                       ");
		sql.append("        ?                         ");
		sql.append("      , ?                         ");
		sql.append("      , ?                         ");
		sql.append("      , ?                         ");
		sql.append("      , ?                         ");
		sql.append("      , ?                         ");
		sql.append("      , ?                         ");
		sql.append("      , ?                         ");
		sql.append(" )                                ");
		PreparedStatement stmt = con.prepareStatement(sql.toString());

		// 条件セット
		stmt.setString(1, product.seller);
		stmt.setShort(2, product.slot);
		stmt.setInt(3, product.price);
		stmt.setInt(4, product.amount);
		stmt.setString(5, product.itemId);
		stmt.setString(6, product.data);
		stmt.setShort(7, logType);
		stmt.setDate(8, new Date(System.currentTimeMillis()));

		// SQL文実行
		int count = stmt.executeUpdate();

		// クローズ
		stmt.close();
		return count == 1;
	}

	/**
	 * ログを保存
	 * @param itemId アイテムID
	 */
	public static double getAveragePrice(String itemId) throws SQLException {

		try (Connection con = ReDivMarche.dataSource.getConnection()) {

			// SQL文作成
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT SUM(price)  as ttl_price  ");
			sql.append("      , SUM(amount) as ttl_amount ");
			sql.append(" FROM   DiverseUserShopLog        ");
			sql.append(" WHERE  proc_time >= (NOW() - INTERVAL '1 MONTH') ");
			sql.append(" AND    proc_type  = ?            ");
			sql.append(" AND    itemId     = ?            ");
			sql.append(" GROUP BY itemid                  ");
			PreparedStatement stmt = con.prepareStatement(sql.toString());

			// 条件セット
			stmt.setShort(1, ShopDataManager.LOG_TYPE_BOUGHT);
			stmt.setString(2, itemId);

			// SQL文実行
			ResultSet result = stmt.executeQuery();

			// データ読み込み
			double average = 0.0d;
			if (result.next()) {
				long price = result.getLong("ttl_price");
				long amount = result.getLong("ttl_amount");

				average = ((double) price / (double) amount);
			}

			// クローズ
			result.close();
			stmt.close();

			return average;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0.0d;
		}
	}
}
