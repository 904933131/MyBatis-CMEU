package pers.cmeu.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.models.HistoryConfig;
import pers.cmeu.models.HistoryConfigCVF;
import pers.cmeu.view.AlertUtil;

public class HistoryConfigController extends BaseController {
	private Logger log=Logger.getLogger(HistoryConfigController.class.getName());
	private IndexController indexController;

	@FXML
	private TableView<HistoryConfigCVF> tblConfigInfo;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.debug("初始化配置信息窗口....");
		tblConfigInfo.setPlaceholder(new Label("尚未添加任何配置信息;可以通过首页保存配置新增"));
		log.debug("初始化配置信息窗口完成!");
		initTable();
	}
	
	/**
	 * 初始化配置table
	 */
	public void initTable(){
		log.debug("初始化配置信息表格...");
		ObservableList<HistoryConfigCVF> data = null;
		try {
			data = getHistoryConfig();
		} catch (Exception e) {
			tblConfigInfo.setPlaceholder(new Label("加载配置文件失败!失败原因:\r\n" + e.getMessage()));
			log.error("初始化配置信息表格出现异常!!!"+e);
		}
		
		TableColumn<HistoryConfigCVF,String> tdInfo =new TableColumn<HistoryConfigCVF, String>("配置信息文件名");	
		TableColumn<HistoryConfigCVF,String> tdOperation=new TableColumn<HistoryConfigCVF, String>("操作");	
		
		tdInfo.setPrefWidth(320);
		tdInfo.setCellValueFactory(new PropertyValueFactory<>("name"));

		tdOperation.setPrefWidth(198);
		tdOperation.setCellValueFactory(new PropertyValueFactory<>("hbox"));
		
		tblConfigInfo.getColumns().add(tdInfo);
		tblConfigInfo.getColumns().add(tdOperation);
		
		tblConfigInfo.setItems(data);
		log.debug("初始化配置信息完成!");

	}

	/**
	 * 获得配置文件Table
	 * 特别注意,条件添加的关系,加载与删除配置需要在这里面操作
	 * @return
	 * @throws Exception
	 */
	public ObservableList<HistoryConfigCVF> getHistoryConfig() throws Exception {
		ObservableList<HistoryConfigCVF> result = FXCollections.observableArrayList();
		List<HistoryConfig> item = ConfigUtil.getHistoryConfigs();
		// 遍历配置文件并加载到工厂里面,同时给操作配置文件的加载与删除

		for (HistoryConfig tmp : item) {
			String configName = tmp.getHistoryConfigName();
			HBox box = new HBox();
			box.setSpacing(15);
			Button button = new Button("加载配置");
			button.setUserData(tmp.getHistoryConfigName());
			button.setOnAction(Event -> {
				try {
					log.debug("执行将配置信息加载到首页...");
					indexController.loadIndexConfigInfo(button.getUserData().toString());
					closeDialogStage();
					log.debug("将配置信息加载到首页成功!");
				} catch (Exception e) {
					AlertUtil.showErrorAlert("加载配置失败!失败原因:\r\n"+e.getMessage());
					log.error("将配置信息加载到首页失败!!!"+e);
				}
			});
			Button button1 = new Button("删除配置");
			button1.setUserData(tmp.getHistoryConfigName());
			button1.setOnAction(Event -> {
				if (AlertUtil.showConfirmAlert("确定删除吗?")) {
					try {
						log.debug("执行删除配置信息...");
						ConfigUtil.deleteHistoryConfigByName(button1.getUserData().toString());
						for (int i = 0; i < tblConfigInfo.getItems().size(); i++) {
							if (tblConfigInfo.getItems().get(i).getName().equals(button1.getUserData().toString())) {
								tblConfigInfo.getItems().remove(i);
								break;
							}
						}
						log.debug("执行删除配置完成!");
					} catch (Exception e) {
						AlertUtil.showErrorAlert("删除失败!失败原因:\r\n"+e.getMessage());
						log.error("执行删除配置失败!!!"+e);
					}
				}
			});
			box.getChildren().addAll(button, button1);

			HistoryConfigCVF cvf = new HistoryConfigCVF(configName, box);
			result.add(cvf);
		}

		return result;
	}

	// -------------------get/set------------------------------

	public IndexController getIndexController() {
		return indexController;
	}

	public void setIndexController(IndexController indexController) {
		this.indexController = indexController;
	}

	public TableView<HistoryConfigCVF> getTblConfigInfo() {
		return tblConfigInfo;
	}

	public void setTblConfigInfo(TableView<HistoryConfigCVF> tblConfigInfo) {
		this.tblConfigInfo = tblConfigInfo;
	}
	
	

	
}
