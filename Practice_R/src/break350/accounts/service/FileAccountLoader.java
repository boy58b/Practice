package break350.accounts.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jxl.Sheet;
import jxl.Workbook;
import break350.accounts.Configs;
import break350.accounts.model.Account;
import break350.accounts.model.Days;

public class FileAccountLoader implements AccountLoader {

	public FileAccountLoader() {
	}

	public ObservableList<Account> load() {
		ObservableList<Account> list = FXCollections.observableArrayList();
		try {
			String fileName = Configs.getProperties().getProperty(
					Configs.pathToEmployeesFile);
			String encodingForEmployees = Configs.getProperties().getProperty(
					Configs.encodingForEmployees);
			Reader r = new InputStreamReader(new FileInputStream(fileName),
					encodingForEmployees);
			@SuppressWarnings("resource")
			BufferedReader inputStream = new BufferedReader(r);
			String row;
			int index = 1;
			while ((row = inputStream.readLine()) != null) {
				String[] spl = row.split("\t");
				String name = spl[0];
				int own = Integer.parseInt(spl[1]);
				int hospital = Integer.parseInt(spl[2]);
				double salary = Double.parseDouble(spl[3]);
				Account ac = new Account(index++, name, own, hospital, salary,
						Days.getWorkingDay());
				list.add(ac);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
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
