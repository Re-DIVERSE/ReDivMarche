package com.github.aburaagetarou.redivmarche;

import com.github.aburaagetarou.redivmarche.shop.ShopDataManager;
import com.github.aburaagetarou.redivmarche.util.Utilities;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public final class ReDivMarche extends JavaPlugin {

	// プラグイン インスタンス
	public static ReDivMarche instance;

	// データソース
	public static HikariDataSource dataSource;

	/**
	 * プラグイン有効化
	 */
	@Override
	public void onEnable() {

		// プラグイン インスタンスをセット
		instance = this;

		// コンフィグを読み込んでDBに接続
		connect();
	}

	/**
	 * プラグイン無効化
	 */
	@Override
	public void onDisable() {

		// 解放
		dataSource.close();
	}

	/**
	 * コンフィグを読み込んでDBに接続
	 */
	public void connect(){

		// 存在しない場合、デフォルトの設定ファイルを作成
		saveDefaultConfig();

		// コンフィグを再読み込み
		reloadConfig();

		// 設定内容を取得
		String address  = getConfig().getString("server.address");
		int    port     = getConfig().getInt("server.port");
		String user     = getConfig().getString("server.user");
		String pass     = getConfig().getString("server.password");
		String schema   = getConfig().getString("server.schema");
		String driver   = getConfig().getString("database.class_name");
		int    lifespan = getConfig().getInt("database.lifespan");

		// Nullチェック
		if(!Utilities.strNullCheck(address, user, pass, schema, driver)){
			throw new IllegalStateException("いずれかの設定が正しくありません。");
		}

		// ポートチェック
		if(!Utilities.portCheck(port)){
			throw new IllegalStateException("ポート番号の設定が正しくありません。");
		}

		// ライフスパンチェック
		if(lifespan == 0){
			throw new IllegalStateException("接続維持時間の設定が正しくありません。");
		}

		// データソースに接続
		dataSource = new HikariDataSource();

		// JDBCのURLを設定
		dataSource.setDriverClassName(driver);
		String url = String.format(
				"jdbc:postgresql://%s:%d/%s?user=%s&password=%s&useSSL=false",
				address,
				port,
				schema,
				user,
				pass
		);
		dataSource.setJdbcUrl(url);

		// ライフスパン
		if(lifespan > 0) {
			dataSource.setMaxLifetime(TimeUnit.MINUTES.toMillis(lifespan));
		}

		try {
			ShopDataManager.createTable();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
