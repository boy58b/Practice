package break350.accounts.service;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jxl.Sheet;
import jxl.Workbook;
import break350.accounts.Configs;
import break350.accounts.model.Account;
import break350.accounts.model.Days;

public class XLSAccountLoader implements AccountLoader {

	@Override
	
	public ObservableList<Account> load() {
		ObservableList<Account> list = FXCollections.observableArrayList();
		String fileName = Configs.getProperties().getProperty(
				Configs.pathToEmployeesFile);
		File inputWorkbook = new File(fileName);
		Workbook w;
		try {
			System.out.println(inputWorkbook);
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines

			for (int i = 0; i < sheet.getRows(); i++) {
				String name = sheet.getCell(0, i).getContents();
				int own = Integer.parseInt(sheet.getCell(1, i).getContents());
				int hospital = Integer.parseInt(sheet.getCell(2, i)
						.getContents());
				double salary = Double.parseDouble(sheet.getCell(3, i)
						.getContents());
				Account ac = new Account(i + 1, name, own, hospital, salary,
						Days.getWorkingDay());
				list.add(ac);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	};

	//@Override
	
	public ObservableList<Account> load(File inputWorkbook) {
		ObservableList<Account> list = FXCollections.observableArrayList();
		Workbook w;
		try {
			System.out.println(inputWorkbook);
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines
           int k = 0, i=7;
			while(sheet.getCell(2, i).getContents()!="")
			{
				String name = sheet.getCell(4, i).getContents();
				int own = Integer.parseInt(sheet.getCell(7, i).getContents());
				int hospital = Integer.parseInt(sheet.getCell(9, i)
						.getContents());
				double salary = Double.parseDouble(sheet.getCell(11, i)
						.getContents());
				Account ac = new Account(k + 1, name, own, hospital, salary,
						Days.getWorkingDay());
				list.add(ac);
				i+=2;
				k++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
