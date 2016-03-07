package beta;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import java.util.*;

/**
 * <p> Простой JList, который поддерживает фильтрацию
 * @author Nathan Stevens
 */
@SuppressWarnings("serial")
public class FilteringJList extends JList<Object> { // Пользвательская JList модель
	private JTextField input;
	
	FilteringModel model;

	public FilteringJList() {
		super.setBorder(new EmptyBorder(5,5, 5, 5));
		model = new FilteringModel();
		setModel(model);
	}

	public void clear() {
		//model.list.clear();
		
		model = new FilteringModel();
		setModel(model);
		installJTextField(getInput());
	}

	
	public void installJTextField(JTextField input) {
		if (input != null) {
			this.setInput(input);
			FilteringModel model = (FilteringModel) getModel();
			input.getDocument().addDocumentListener(model);
		}
	}

	public void uninstallJTextField(JTextField input) {
		if (input != null) {
			FilteringModel model = (FilteringModel) getModel();
			input.getDocument().removeDocumentListener(model);
			this.setInput(null);
		}
	}

	public void setModel(ListModel<Object> model) {
		if (!(model instanceof FilteringModel)) {
			throw new IllegalArgumentException();
		} else {
			super.setModel(model);
		}
	}

	public void addElements(ArrayList<String> arr) {
		for (int i = 0; i < arr.size(); i++) ((FilteringModel) getModel()).addElement(arr.get(i));
	}
	
	public void addElements(String[] arr) {
		for (int i = 0; i < arr.length; i++) ((FilteringModel) getModel()).addElement(arr[i]);
	}

	public JTextField getInput() {
		return input;
	}

	public void setInput(JTextField input) {
		this.input = input;
	}

	private class FilteringModel extends AbstractListModel<Object> implements
	DocumentListener {
		List<Object> list;
		List<Object> filteredList;
		String lastFilter = "";

		public FilteringModel() {
			list = new ArrayList<Object>();
			filteredList = new ArrayList<Object>();
		}

		public void addElement(Object element) {
			list.add(element);
			filter(lastFilter);
		}

		public int getSize() {
			return filteredList.size();
		}

		public Object getElementAt(int index) {
			Object returnValue;
			if (index < filteredList.size()) {
				returnValue = filteredList.get(index);
			} else {
				returnValue = null;
			}
			return returnValue;
		}

		void filter(String search) {
			filteredList.clear();
			for (Object element : list) {
				if (element.toString().toLowerCase().indexOf(search.toLowerCase(), 0) != -1) {
					filteredList.add(element);
				}
			}
			fireContentsChanged(this, 0, getSize());
		}

		public void insertUpdate(DocumentEvent event) {
			Document doc = event.getDocument();
			try {
				lastFilter = doc.getText(0, doc.getLength());
				filter(lastFilter);
			} catch (BadLocationException ble) {
				System.err.println("Bad location: " + ble);
			}
		}

		public void removeUpdate(DocumentEvent event) {
			Document doc = event.getDocument();
			try {
				lastFilter = doc.getText(0, doc.getLength());
				filter(lastFilter);
			} catch (BadLocationException ble) {
				System.err.println("Bad location: " + ble);
			}
		}

		public void changedUpdate(DocumentEvent event) {}
	}
}