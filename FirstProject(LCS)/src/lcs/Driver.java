/**
 * @author: Ameer Eleyan
 * ID: 1191076
 * At: 10/29/2021   1:48 AM
 */
package lcs;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Driver implements Initializable {

    public int[] LEDs;
    private int size = 0;
    private int index = 0;

    @FXML // fx:id="HBoxAdd"
    private HBox HBoxAdd; // Value injected by FXMLLoader

    @FXML // fx:id="HBoxSet"
    private HBox HBoxSet; // Value injected by FXMLLoader

    @FXML // fx:id="btTryAnotherData"
    private Button btTryAnotherData; // Value injected by FXMLLoader

    @FXML // fx:id="btUploadData"
    private Button btUploadData; // Value injected by FXMLLoader

    @FXML // fx:id="btFind"
    private Button btFind; // Value injected by FXMLLoader

    @FXML // fx:id="comboBox"
    private ComboBox<String> comboBox; // Value injected by FXMLLoader

    @FXML // fx:id="textAreaForData"
    private TextArea textAreaForData; // Value injected by FXMLLoader

    @FXML // fx:id="textAreaForResult"
    private TextArea textAreaForResult; // Value injected by FXMLLoader

    @FXML // fx:id="textAreaDetails"
    private TextArea textAreaDetails; // Value injected by FXMLLoader

    @FXML // fx:id="textAreaResults"
    private TextArea textAreaResults; // Value injected by FXMLLoader

    @FXML // fx:id="txtData"
    private TextField txtData; // Value injected by FXMLLoader

    @FXML // fx:id="txtDataSize"
    private TextField txtDataSize; // Value injected by FXMLLoader

    private String strRes="";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.comboBox.getItems().add("Manual");
        this.comboBox.getItems().add("Using File");
        this.returnControlsDefault();
    }

    // Handle Combo box
    public void handleComboBox() {
        if (comboBox.getValue().equals("Manual")) {
            this.HBoxSet.setDisable(false);
            this.btUploadData.setDisable(true);
        } else {
            this.btUploadData.setDisable(false);
            this.HBoxSet.setDisable(true);
            this.HBoxAdd.setDisable(true);
        }
    }

    // Handle set button
    public void handleBtSet() {
        if (this.txtDataSize.getText().trim().isEmpty()) { // To check the text Field if contains value
            Message.displayMessage("Warning", "Please Enter the data size");
            this.txtDataSize.clear();
        } else if (isNumber(this.txtDataSize.getText().trim())) {// To check value if it is a number

            // Creating matrix with a size that was entered
            this.size = Integer.parseInt(this.txtDataSize.getText().trim());
            this.LEDs = new int[this.size];

            this.textAreaForData.setText("Data size is: " + this.size + ", and they are:\n");
            this.HBoxSet.setDisable(true);
            this.comboBox.setDisable(true);
            this.HBoxAdd.setDisable(false);
        } else {
            Message.displayMessage("Warning", "Please enter a valid size");
            this.txtDataSize.clear();
        }
    }

    // Handle add button
    public void handleBtAdd() {
        String data = this.txtData.getText().trim();
        if (data.isEmpty()) { // To check the text Field if contains value
            Message.displayMessage("Warning", "Please Enter the data");
            this.txtData.clear();
            return;
        } else if (isNumber(data)) { // To check value if it is a number

            if (Integer.parseInt(data) <= this.size) { // To validate value

                this.LEDs[this.index] = Integer.parseInt(data);
                this.textAreaForData.appendText(" \nLed # " + this.LEDs[this.index] + "\t\tPower- " + (this.index + 1) + "\n");
                this.index++;

            } else
                Message.displayMessage("Warning", "The value must be less than or equal" + this.size);

        } else {
            Message.displayMessage("Warning", "Please enter a valid data");
        }

        this.txtData.clear(); // te receive another value

        if (this.index == this.size) { // All data was entered
            this.HBoxAdd.setDisable(true);
            this.btFind.setDisable(false);
        }
    }

    // Handle upload data button
    public void handleUploadData() {
        this.uploadFiles();
    }

    // upload files using a browser8
    private void uploadFiles() {

        // File Chooser
        FileChooser fileChooser = new FileChooser(); //File to upload
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Files", "*.txt"); // Specifying the extension of the files
        fileChooser.getExtensionFilters().add(extFilter); // Appends the specified element to the end of this list
        File file = fileChooser.showOpenDialog(GUI.window.getScene().getWindow()); // Browsing one file as .txt

        if (file != null) { // To check if the user select the file or close the window without selecting
            this.readDataFromFile(file);
        }
    }

    // Methode to read data from file
    private void readDataFromFile(File fileName) {
        try {
            Scanner input = new Scanner(fileName); // instance of scanner for read data from file
            if (fileName.length() == 0) {// no data in file
                Message.displayMessage("Warning", "  There are no data in the file " + fileName + " ");

            } else {
                boolean firstValue = true; // represent the size of the data
                while (input.hasNext()) { // Read file value by value

                    if (input.hasNextInt()) { // File have contains a data
                        if (firstValue) { // To check the size of the data if it is specified or not
                            this.size = input.nextInt();
                            this.LEDs = new int[this.size];
                            this.textAreaForData.setText("Data size is: " + this.size + ", and they are:\n");
                            firstValue = false;
                        } else { // The size of the data was specified, thus starting read them
                            int data = input.nextInt();
                            if(index < this.size){
                                if (data <= this.size) { // To validate value
                                   if(!isFound(this.LEDs, data)){
                                       this.LEDs[this.index] = data;
                                       this.textAreaForData.appendText(" \nLed #" + this.LEDs[this.index] + "\t\tPower-" + (this.index + 1) + "\n");
                                       this.index++;
                                   }else{
                                       Message.displayMessage("Warning", data+" Is already exit");
                                   }

                                } else {
                                    Message.displayMessage("Warning", data + " is invalid");
                                }
                            }else{
                                Message.displayMessage("Warning","The number leds larger than it's size\nSo we take the first "+index+" LEds");
                            }

                        }
                    } else {
                        input.next(); // To ignore anything not an integer
                    }
                }
                input.close(); // prevent(close) scanner to read data
                this.btFind.setDisable(false);
                this.comboBox.setDisable(true);
                this.btUploadData.setDisable(true);
            }
        } catch (FileNotFoundException e) {
            // The specific file for reading data does not exist
            Message.displayMessage("Error", " The system can NOT find the file " + fileName + "  ");
        }
    }

    // Handle find pairs button
    public void handleFind() {
        this.longestCommonSubsequence();
        this.btTryAnotherData.setDisable(false);
        this.btFind.setDisable(true);
    }


    // Handle try another data button
    public void handleBtTryAnotherData() {
        this.returnControlsDefault();
    }

    // To check the value of the entered numberOfShares if contain only digits or not
    public static boolean isNumber(String number) {
        /* To check the entered number of shares, that it consists of
           only digits
         */
        try {
            return number.matches("\\d+") && Integer.parseInt(number) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Handle try another data button
    private void returnControlsDefault() {
        this.comboBox.setDisable(false);
        this.HBoxSet.setDisable(true);
        this.HBoxAdd.setDisable(true);
        this.btUploadData.setDisable(true);
        this.btFind.setDisable(true);
        this.btTryAnotherData.setDisable(true);
        this.size = 0;
        this.index = 0;
        this.textAreaDetails.clear();
        this.textAreaForData.clear();
        this.textAreaForResult.clear();
        this.textAreaResults.clear();
        this.btTryAnotherData.setDisable(true);
    }

    /**
     * Find the longest common subsequence between two sequences
     * using  Dynamic programming approach which takes the O (N * M) time complexity,
     * the following relation represent the code. Then Display the result
     * <p>
     *                      ________
     *                     |
     *                     |  0                                                        if ( i = 0 ) OR ( j = 0 )
     * costForLCS[i.j]  =  |
     *                     |  costForLCS [ i-1, j-1 ] + 1                              if xi = yj
     *                     |
     *                     |  max ( costForLCS [ i-1, j ], costForLCS [ i , j-1 ] )    if xi ≠ yj
     *                     |________
     **/
    public void longestCommonSubsequence() {

        int[][] costForLCS = new int[this.size + 1][this.size + 1];
        byte[][] tempArrayForDisplayResult = new byte[this.size + 1][this.size + 1];

        for (int i = 1; i <= this.size; i++) {

            for (int j = 1; j <= this.size; j++) {

                if (i == this.LEDs[j - 1]) { // Second case from the above relation
                    costForLCS[i][j] = costForLCS[i - 1][j - 1] + 1;
                    tempArrayForDisplayResult[i][j] = 1;

                } else {// Third case from the above relation
                    if (costForLCS[i][j - 1] > costForLCS[i - 1][j]) {
                        costForLCS[i][j] = costForLCS[i][j - 1];
                        tempArrayForDisplayResult[i][j] = 0;
                    } else {
                        costForLCS[i][j] = costForLCS[i - 1][j];
                        tempArrayForDisplayResult[i][j] = 2;
                    }
                }
            }
        }

        displayAllLCS(tempArrayForDisplayResult, this.size, this.size, costForLCS);

        // View details of the results
        this.textAreaDetails.appendText("\t");
        this.textAreaResults.appendText("\t");
        for (int i = 0; i < this.size + 1; i++) {

            for (int j = 0; j < this.size + 1; j++) {

                /* The first row and first column which was zero value will be skipped
                    and instead of them will be filled with Led word in the row,
                    and Power word in the column
                */
                if (i == 0 && j < this.size) {
                    textAreaDetails.appendText("\t\t" + "Led-" + this.LEDs[j]);
                    textAreaResults.appendText("\t\t" + "Led-" + this.LEDs[j]);
                } else if (j == 0) {
                    textAreaDetails.appendText("Power-" + i + "\t\t");
                    textAreaResults.appendText("Power-" + i + "\t\t");
                }

                if ((j > 0) && (i > 0)) {// Display values after fill first row and first column
                    char arrow = '←';
                    if (tempArrayForDisplayResult[i][j] == 1) {
                        arrow = '↖';
                    } else if (tempArrayForDisplayResult[i][j] == 2) {
                        arrow = '↑';
                    }
                    textAreaDetails.appendText(" " + arrow + " \t  \t  \t");
                    textAreaResults.appendText(" " + costForLCS[i][j] + " \t  \t  \t");
                }

            }

            this.textAreaDetails.appendText("\n");
            this.textAreaResults.appendText("\n");
        }


    }

    // Display all longest common subsequence
    public void displayAllLCS(byte[][] b, int i, int j, int[][] costForLCS) {

        int row = i; // to looping for all row have the LCS number(costForLCS[size][size])
        int column = j;// to looping for all column have the LCS number(costForLCS[size][size])

        // To store all unique LCS pairs
        LinkedList<String> allLCS = new LinkedList<>();
        // To visit through column rows that contain LCS number
        while (costForLCS[row][column] == costForLCS[i][j]) { // To check if the current number in the costForLCS matrix equal the LCS length

            // To visit through column that contain LCS number
            while (costForLCS[row][column] == costForLCS[i][j]) { // To check if the current number in the costForLCS matrix equal the LCS length

                int tempRow = row;
                int tempColumn = column;
                /* We used the above two variable to visit the paths,
                but the initial value for them start from
                the current row and column, so will be changed
                in every row and column */

                String pairs = ""; // To store the LCS LEDs

                while (tempRow != 0 && tempColumn != 0) { // To visit the path that represent LCS pairs
                    if (b[tempRow][tempColumn] == 1) {
                        if(strRes.length()<costForLCS[costForLCS.length-1][costForLCS.length-1]){
                            this.strRes = tempRow + strRes;
                        }
                        pairs = "{Power-" + tempRow + ", Led-" + tempRow + "}\n" + pairs;
                        tempRow--; // to move the previous row
                        tempColumn--; // to move the previous column
                    } else if (b[tempRow][tempColumn] == 0) tempColumn--;
                    else tempRow--;
                }


                if (!allLCS.contains(pairs)) {
                    allLCS.addFirst(pairs);
                    this.textAreaForResult.appendText("The best pairs to be connected are:\n");
                    this.textAreaForResult.appendText(pairs);
                    this.textAreaForResult.appendText("\nThus " + costForLCS[row][column] + " LEDs will be lighted");
                    this.textAreaForResult.appendText("\n\n----------------------OR----------------------\n\n");
                }

                column--;
            }
            row--;
            column = j; // To start from last column in the new row(previous row of the current row)
        }

    }

    // Another DP Algorithm the longest increasing substance
    private String lengthOfLIS(int[] leds) {

        if (leds.length == 0) return null;

        // for the longest increasing
        int[] lis = new int[leds.length];

        // used to print the all LIS
        int[] indices = new int[leds.length];

        // longest increasing sub-sequence having just one element has length 1
        Arrays.fill(lis, 1);
        Arrays.fill(indices, -1);

        // start from second element in the array
        for (int i = 1; i < leds.length; i++) {

            // do for each element in sub-array leds[0..i-1]
            for (int j = 0; j < i; j++) {
                if (leds[i] > leds[j] && lis[i] < lis[j] + 1) {
                    lis[i] = lis[j] + 1;
                    // store what index helped to extend lis[i]
                    indices[i] = j;
                }
            }
        }
        // find the maximum from lis array and it's index
        int max = lis[0], maxIndex = 0;
        for (int i = 1; i < leds.length; i++) {
            if (max <= lis[i]) {
                max = lis[i];
                maxIndex = i;
            }
        }

        //starting from index of max-length LIS traverse back
        //using indices array populated earlier
        String str = "";
        while (maxIndex >= 0) {
            str = "{Power-" + leds[maxIndex] + ", Led-" + leds[maxIndex] + "}\n" + str;

            maxIndex = indices[maxIndex];
        }
        return str;
    }

    private static boolean isFound(int [] array, int num){
        for (int j : array) {
            if (j == num) return true;
        }
        return false;
    }
}
