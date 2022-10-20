package com.example.labassignment1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;


class CurrencyConversions {
  private static class CurrencyConversionItem {
    private final String from;
    private final String to;
    private final Float rate;

    CurrencyConversionItem(String from, String to, Float rate) {
      this.from = from;
      this.to = to;
      this.rate = rate;
    }
  }

  private CurrencyConversionItem selectedConversion = new CurrencyConversionItem("", "", 1.0f);
  private final HashMap<String, CurrencyConversionItem> conversionsMap = new HashMap<>();

  public final ArrayList<String> froms = new ArrayList<>();
  public final ArrayList<String> tos = new ArrayList<>();

  private void updateFroms() {
    froms.clear();
    String from;
    for (CurrencyConversionItem conversion : conversionsMap.values()) {
      from = conversion.from;
      if (!froms.contains(from) && !from.equals(selectedConversion.to)) {
        froms.add(from);
      }
    }
  }

  private void updateTos() {
    tos.clear();
    String to;
    for (CurrencyConversionItem conversion : conversionsMap.values()) {
      to = conversion.to;
      if (!tos.contains(to) && !to.equals(selectedConversion.from)) {
        tos.add(to);
      }
    }
  }

  public void addConversion(String from, String to, Float rate) {
    conversionsMap.put(from + to, new CurrencyConversionItem(from, to, rate));
    conversionsMap.put(to + from, new CurrencyConversionItem(to, from, 1 / rate));
    updateFroms();
    updateTos();
  }

  public void updateSelectedConversion(String from, String to) {
    if (!from.isEmpty() && !to.isEmpty()) {
      selectedConversion = conversionsMap.get(from + to);
      updateFroms();
      updateTos();
    }
  }

  public Float convert(Float valueFrom) {
    return valueFrom * selectedConversion.rate;
  }
}

public class MainActivity extends AppCompatActivity {

  CurrencyConversions conversions = new CurrencyConversions();

  ArrayAdapter<String> adapterFrom;
  String selectionFrom = "";
  Spinner spinnerFrom;

  ArrayAdapter<String> adapterTo;
  String selectionTo = "";
  Spinner spinnerTo;

  EditText input;
  EditText output;

  Button switchButton;
  Button convertButton;

  Toast emptyInputToast;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    conversions.addConversion(
        getResources().getString(R.string.currency_us),
        getResources().getString(R.string.currency_eg),
        Float.valueOf(getResources().getString(R.string.exchange_rate_us_to_eg))
    );
    // The two below are just proof of concept but not required in the lab
    conversions.addConversion(
        getResources().getString(R.string.currency_uk),
        getResources().getString(R.string.currency_eg),
        Float.valueOf(getResources().getString(R.string.exchange_rate_uk_to_eg)))
    ;
    conversions.addConversion(
        getResources().getString(R.string.currency_us),
        getResources().getString(R.string.currency_uk),
        Float.valueOf(getResources().getString(R.string.exchange_rate_us_to_uk))
    );

    adapterFrom = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conversions.froms);
    adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinnerFrom = (Spinner) findViewById(R.id.spinner_from);
    spinnerFrom.setAdapter(adapterFrom);
    spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectionFrom = parent.getItemAtPosition(pos).toString();
        conversions.updateSelectedConversion(selectionFrom, selectionTo);
        adapterFrom.notifyDataSetChanged();
        adapterTo.notifyDataSetChanged();
        // Manually re-selecting the selections to account for changes in dataset
        spinnerFrom.setSelection(adapterFrom.getPosition(selectionFrom));
        spinnerTo.setSelection(adapterTo.getPosition(selectionTo));
        // Calling setText to trigger "onTextChanged"
        input.setText(input.getText());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Not sure when this could happen and what to do if it happens
      }
    });

    adapterTo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conversions.tos);
    adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinnerTo = (Spinner) findViewById(R.id.spinner_to);
    spinnerTo.setAdapter(adapterTo);
    spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectionTo = parent.getItemAtPosition(pos).toString();
        conversions.updateSelectedConversion(selectionFrom, selectionTo);
        adapterFrom.notifyDataSetChanged();
        adapterTo.notifyDataSetChanged();
        // Manually re-selecting the selections to account for changes in dataset
        spinnerFrom.setSelection(adapterFrom.getPosition(selectionFrom));
        spinnerTo.setSelection(adapterTo.getPosition(selectionTo));
        // Calling setText to trigger "onTextChanged"
        input.setText(input.getText());
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Not sure when this could happen and what to do if it happens
      }
    });

    switchButton = (Button) findViewById(R.id.switch_button);
    switchButton.setOnClickListener(view -> {
      String temp = selectionFrom;
      selectionFrom = selectionTo;
      selectionTo = temp;
      conversions.updateSelectedConversion(selectionFrom, selectionTo);
      adapterFrom.notifyDataSetChanged();
      adapterTo.notifyDataSetChanged();
      // Manually re-selecting the selections to account for changes in dataset
      spinnerFrom.setSelection(adapterFrom.getPosition(selectionFrom));
      spinnerTo.setSelection(adapterTo.getPosition(selectionTo));
      // Calling setText to trigger "onTextChanged"
      input.setText(input.getText());
    });

    input = (EditText) findViewById(R.id.input_value);
    output = (EditText) findViewById(R.id.output_value);
    input.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      @SuppressLint("SetTextI18n")
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (input.getText().length() > 0) {
          Float inputValue = Float.valueOf(input.getText().toString());
          Float outputValue = conversions.convert(inputValue);
          output.setText(outputValue.toString());
        } else {
          output.getText().clear();
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    emptyInputToast = Toast.makeText(this, "Please enter the value to convert from", Toast.LENGTH_LONG);

    convertButton = (Button) findViewById(R.id.convert_button);
    convertButton.setOnClickListener(view -> {
      Editable text = input.getText();
      if (text.length() == 0 && !emptyInputToast.getView().isShown()) {
        // If text was empty and prompt is not already shown, prompt the user to enter something
        emptyInputToast.show();
      } else {
        // Otherwise, calling setText to trigger "onTextChanged"
        input.setText(text);
      }
    });

  }
}
