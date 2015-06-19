package break350.accounts.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import break350.accounts.Configs;
import break350.accounts.dao.AccountDao;
import break350.accounts.dao.AccountDaoImpl;
import break350.accounts.model.Account;
import break350.accounts.model.AccountSummary;
import break350.accounts.model.Days;
import break350.accounts.model.Daysable;
import break350.accounts.model.Rate;
import break350.accounts.model.Rateable;
import break350.accounts.report.Report;

public class MainController implements Initializable, Rateable, Daysable {
	private Stage stage;
	@FXML
	private TextField rate;
	@FXML
	private Button print;
	@FXML
	private Button export;
	@FXML
	private Button imports;
	@FXML
	private Button months;
	@FXML
	private Button currRate;
	@FXML
	private Label working;
	@FXML
	private ComboBox<String> exportFormat;
	@FXML
	private TableView<Account> table;
	@FXML
	private TableColumn<Account, Integer> index;
	@FXML
	private TableColumn<Account, String> name;
	@FXML
	private TableColumn<Account, Integer> worked;
	@FXML
	private TableColumn<Object, Integer> own;
	@FXML
	private TableColumn<Object, Integer> hospital;
	@FXML
	private TableColumn<Account, Double> salary;
	@FXML
	private TableColumn<Account, Double> eur;
	@FXML
	private TableColumn<Account, Double> uah;
	@FXML
	private AccountDao accountDao;
	@FXML
	private TableView<AccountSummary> summary;
	@FXML
	private TableColumn<AccountSummary, String> text;
	@FXML
	private TableColumn<AccountSummary, Integer> sumworked;
	@FXML
	private TableColumn<AccountSummary, Integer> sumown;
	@FXML
	private TableColumn<AccountSummary, Integer> sumhospital;
	@FXML
	private TableColumn<AccountSummary, Double> sumsalary;
	@FXML
	private TableColumn<AccountSummary, Double> sumeur;
	@FXML
	private TableColumn<AccountSummary, Double> sumuah;
	

	public void initialize(URL location, ResourceBundle resources) {
		setCellsValueFactorys();
		setCellsFactorys();
		setOnActions();
		
		accountDao = new AccountDaoImpl();

		Rate.addAllRateable(this, accountDao);
	    Rate.loadFromWeb();

		Days.addAllDaysable(this, accountDao);
		Days.loadDays();
		Days.initPastMonth();
		UpdateTable();
		ResizeWindow();
        summary.widthProperty().addListener(new ChangeListener<Number>() {
    @Override
    public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
        Pane header = (Pane) summary.lookup("TableHeaderRow");
        if (header.isVisible()){
            header.setMaxHeight(0);
            header.setMinHeight(0);
            header.setPrefHeight(0);
            header.setVisible(false);
        }
    }
});
	}
	private void ResizeWindow(){
		
	text.setPrefWidth(index.getWidth()+name.getWidth()+7);
	sumworked.setPrefWidth(worked.getWidth());
	sumown.setPrefWidth(own.getWidth());
	sumhospital.setPrefWidth(hospital.getWidth());
	sumsalary.setPrefWidth(salary.getWidth());
	sumeur.setPrefWidth(eur.getWidth());
	sumuah.setPrefWidth(uah.getWidth());
}

	public void UpdateTable(){
		accountDao.setRate(Double.valueOf(rate.getText()));
		table.setItems(accountDao.getAllAccounts());
		summary.setItems(Account.getSum(accountDao.getAllAccounts()));
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public String getExportType() {
		return exportFormat.getSelectionModel().getSelectedItem();
	}

	private void setOnActions() {
		 uah.widthProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
		   ResizeWindow();
		    }
		});
		
		rate.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				double rate = getRateFromTextField();
				Rate.setRate(rate);
				UpdateTable();
			}
		});
		print.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				Report r = new Report();
				r.print(accountDao.getAllAccounts());
			}
		});
		export.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String type = getExportType();
				if (type != null) {
					FileChooser fch = new FileChooser();
					FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
							type.toUpperCase() + " files", "*." + type);
					fch.getExtensionFilters().add(extFilter);
					File file = fch.showSaveDialog(stage);
					if (file != null) {
						Report r = new Report();
						if (type.equals("xls")) {
							r.exportToXLS(accountDao.getAllAccounts(), file);
						} else if (type.equals("odt")) {
							r.exportToODT(accountDao.getAllAccounts(), file);
						}
					}
				}
			}
		});
		
		imports.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
					FileChooser fch = new FileChooser();
					fch.getExtensionFilters().addAll(
							new FileChooser.ExtensionFilter("MS Excel Document", "*.xls")
							);
					File file = fch.showOpenDialog(stage);
					accountDao = new AccountDaoImpl(file);
					if(file!=null)
					UpdateTable();
			}
		});
		
		
		
		months.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				FXMLLoader loader = new FXMLLoader();
				Stage stage = new Stage();
				File fileMonth = new File(Configs.getProperties().getProperty(
						Configs.pathToMonthFXML));
				try {
					loader.setLocation(fileMonth.toURI().toURL());
					Parent parent;
					try {
						parent = loader.load();
						Scene scene = new Scene(parent);
						stage.setScene(scene);
						stage.initModality(Modality.APPLICATION_MODAL);
						MonthDialogController controller = loader
								.getController();
						controller.setStage(stage);
						stage.showAndWait();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
				UpdateTable();
			}
		});
		currRate.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				Rate.loadFromWeb();
				UpdateTable();
			}
		});
	}

	public double getRateFromTextField() {
		double r = -1;
		try {
			r = Double.parseDouble(this.rate.getText());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return r;
	}

	private void setCellsFactorys() {
		own.setCellFactory(TextFieldTableCell
				.forTableColumn(new IntegerStringConverter()));
		
		own.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Object, Integer>>() {

			public void handle(CellEditEvent<Object, Integer> event) {
				((Account) event.getTableView().getItems()
						.get(event.getTablePosition().getRow())).setOwn(event
						.getNewValue());
				UpdateTable();
			}
		});

		hospital.setCellFactory(TextFieldTableCell
				.forTableColumn(new IntegerStringConverter()));
		hospital.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Object, Integer>>() {

			public void handle(CellEditEvent<Object, Integer> event) {
				((Account) event.getTableView().getItems()
						.get(event.getTablePosition().getRow()))
						.setHospital(event.getNewValue());
				UpdateTable();
			}
		});
	}

	private void setCellsValueFactorys() {
		text.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AccountSummary, String>, ObservableValue<String>>() {

			public ObservableValue<String> call(
					CellDataFeatures<AccountSummary, String> param) {
				return param.getValue().textProperty();
			}
		});
		sumworked.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AccountSummary, Integer>, ObservableValue<Integer>>() {

			public ObservableValue<Integer> call(
					CellDataFeatures<AccountSummary, Integer> param) {
				return param.getValue().sumworkedProperty().asObject();
			}
		});
		
		sumown.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AccountSummary, Integer>, ObservableValue<Integer>>() {

			public ObservableValue<Integer> call(
					CellDataFeatures<AccountSummary, Integer> param) {
				return param.getValue().sumownProperty().asObject();
			}
		});
		sumhospital.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AccountSummary, Integer>, ObservableValue<Integer>>() {

			public ObservableValue<Integer> call(
					CellDataFeatures<AccountSummary, Integer> param) {
				return param.getValue().sumhospitalProperty().asObject();
			}
		});
		sumsalary.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AccountSummary, Double>, ObservableValue<Double>>() {

			public ObservableValue<Double> call(
					CellDataFeatures<AccountSummary, Double> param) {
				return param.getValue().sumsalaryProperty().asObject();
			}
		});

		sumeur.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AccountSummary, Double>, ObservableValue<Double>>() {

			public ObservableValue<Double> call(
					CellDataFeatures<AccountSummary, Double> param) {
				return param.getValue().sumeurProperty().asObject();
			}
		});

		sumuah.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AccountSummary, Double>, ObservableValue<Double>>() {

			public ObservableValue<Double> call(
					CellDataFeatures<AccountSummary, Double> param) {
				return param.getValue().sumuahProperty().asObject();
			}
		});
		
		index.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

			public ObservableValue<Integer> call(
					CellDataFeatures<Account, Integer> param) {
				return param.getValue().indexProperty().asObject();
			}
		});

		name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>() {

			public ObservableValue<String> call(
					CellDataFeatures<Account, String> param) {
				return param.getValue().nameProperty();
			}
		});
		worked.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

			public ObservableValue<Integer> call(
					CellDataFeatures<Account, Integer> param) {
				return param.getValue().workedProperty().asObject();
			}
		});

		own.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, Integer>, ObservableValue<Integer>>() {

			public ObservableValue<Integer> call(
					CellDataFeatures<Object, Integer> param) {
				return ((Account) param.getValue()).ownProperty().asObject();
			}
		});

		hospital.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, Integer>, ObservableValue<Integer>>() {

			public ObservableValue<Integer> call(
					CellDataFeatures<Object, Integer> param) {
				return ((Account) param.getValue()).hospitalProperty()
						.asObject();
			}
		});

		salary.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Double>, ObservableValue<Double>>() {

			public ObservableValue<Double> call(
					CellDataFeatures<Account, Double> param) {
				return param.getValue().salaryProperty().asObject();
			}
		});

		eur.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Double>, ObservableValue<Double>>() {

			public ObservableValue<Double> call(
					CellDataFeatures<Account, Double> param) {
				return param.getValue().eurProperty().asObject();
			}
		});

		uah.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Double>, ObservableValue<Double>>() {

			public ObservableValue<Double> call(
					CellDataFeatures<Account, Double> param) {
				return param.getValue().uahProperty().asObject();
			}
		});
	}

	@Override
	public void setRate(double newRate) {
		rate.setText(String.valueOf(newRate));
	}

	@Override
	public void setWorkingDays(int workingDays) {
		working.setText(String.valueOf(workingDays));
	}

}
