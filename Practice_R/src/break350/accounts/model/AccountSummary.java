package break350.accounts.model;

import break350.accounts.utils.Util;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AccountSummary {
	private SimpleStringProperty text;
	private SimpleIntegerProperty sumworked;
	private SimpleIntegerProperty sumown;
	private SimpleIntegerProperty sumhospital;
	private SimpleDoubleProperty sumsalary;
	private SimpleDoubleProperty sumeur;
	private SimpleDoubleProperty sumuah;
	
	

	public AccountSummary(int sumWorked, int sumOwn, int sumHospital,
			double sumSalary, double sumEur, double sumUah) {
		super();
		this.text = new SimpleStringProperty("Сума :");
		this.sumworked = new SimpleIntegerProperty(sumWorked);
		this.sumown = new SimpleIntegerProperty(sumOwn);
		this.sumhospital = new SimpleIntegerProperty(sumHospital);
		this.sumsalary = new SimpleDoubleProperty(sumSalary);
		this.sumeur = new SimpleDoubleProperty(Util.round(sumEur, 100));
		this.sumuah = new SimpleDoubleProperty(Util.round(sumUah, 100));
		// TODO Auto-generated constructor stub
		//System.out.println(sumeur);
	}

	public SimpleIntegerProperty sumhospitalProperty() {
		return sumhospital;
	}
	public SimpleStringProperty textProperty() {
		return text;
	}
	public SimpleIntegerProperty sumworkedProperty() {
		return sumworked;
	}
	public SimpleDoubleProperty sumsalaryProperty() {
		return sumsalary;
	}
	public SimpleDoubleProperty sumeurProperty() {
		return sumeur;
	}
	public SimpleDoubleProperty sumuahProperty() {
		return sumuah;
	}
	public SimpleIntegerProperty sumownProperty() {
		return sumown;
	}

}
