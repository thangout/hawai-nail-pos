package td.pokladna2.employeedbs;

import androidx.appcompat.app.AppCompatActivity;
import td.pokladna2.FileUtils;
import td.pokladna2.LocalDatabase;
import td.pokladna2.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class AddEmployee extends AppCompatActivity {

    boolean isUpdateState;

    AppDatabase dbs;

    Employee emp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        Intent intent = getIntent();
        int employeeId = intent.getIntExtra("EMPLOYEE_ID",-1);

        dbs = LocalDatabase.getInstance(getApplicationContext()).DBS;

        if (employeeId == -1){
            isUpdateState = false;
        }else{
            loadDataToView(employeeId);
            isUpdateState = true;
        }


        initButtons();
    }

    private void loadDataToView(int employeeId) {

        emp = dbs.employeeDAO().findById(employeeId);
        TextView id = findViewById(R.id.addEmpIdText);
        TextView name = findViewById(R.id.addEmpNameText);
        TextView password = findViewById(R.id.addEmpPasswordText);
        TextView dic = findViewById(R.id.addEmpDicText);
        TextView certificateName = findViewById(R.id.addEmpCertificateNameText);
        TextView certificatePassword = findViewById(R.id.addEmpCertificatePasswordText);
        TextView shopId = findViewById(R.id.addEmpShopIdText);

        id.setText(String.valueOf(employeeId));

        name.setText(emp.getName());
        password.setText(emp.getPassword());
        dic.setText(emp.getDic());
        certificateName.setText(emp.getCertificateName());
        certificatePassword.setText(emp.getCertificatePassword());
        shopId.setText(emp.getShopId());
    }

    private void initButtons() {

        Button openFileButton = findViewById(R.id.openFileButton);

        openFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile(null);
            }
        });

        final Button saveEmployeeButton = findViewById(R.id.saveEmployeeButton);

        saveEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    saveEmployeeToDBS();
            }
        });


        final Button deleteEmp = findViewById(R.id.deleteEmployeeButton);

        deleteEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbs.employeeDAO().delete(emp);
                finish();
            }
        });



    }

    void saveEmployeeToDBS(){
        TextView id = findViewById(R.id.addEmpIdText);
        TextView name = findViewById(R.id.addEmpNameText);
        TextView password = findViewById(R.id.addEmpPasswordText);
        TextView dic = findViewById(R.id.addEmpDicText);
        TextView certificateName = findViewById(R.id.addEmpCertificateNameText);
        TextView certificatePassword = findViewById(R.id.addEmpCertificatePasswordText);
        TextView shopId = findViewById(R.id.addEmpShopIdText);

        int idC = Integer.valueOf(id.getText().toString());
        String nameC = name.getText().toString();
        String passwordC = password.getText().toString();
        String dicC = dic.getText().toString();
        String certificateNameC = certificateName.getText().toString();
        String certificatePasswordC =  certificatePassword.getText().toString();
        String showIdC = shopId.getText().toString();

        Employee employee = new Employee(idC,nameC,passwordC,dicC,certificateNameC,certificatePasswordC,showIdC);

        EmployeeDBS.init(this);

        if (isUpdateState){
            dbs.employeeDAO().updateEmployee(employee);
        }else{
            dbs.employeeDAO().insertEmployee(employee);
        }

        finish();
    }

    private static final int PICKFILE_RESULT_CODE = 1;
    private void openFile(Uri pickerInitialUri) {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

         Uri fileUri;
         String filePath;
         String fileName;
        TextView certificateName = findViewById(R.id.addEmpCertificateNameText);

        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    //filePath = fileUri.getPath();

                    //filePath = getPath(fileUri);
                    filePath = FileUtils.getPath(getApplicationContext(),fileUri);

                    String  a = filePath;
                    String[] splited = a.split("/");
                    fileName = splited[splited.length-1];

                    certificateName.setText(filePath);

                    //getting the file
                    File source = new File(filePath);
                    File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/certs/" + fileName);



                    Log.d("src is / absolute path", source.toString());
                    Log.d("FileName is ",fileName);
                    Log.d("destination is ",destination.toString());

                    try {
                        copy(source,destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //writeFile(file);
                }
                break;
        }
    }

    void writeFile(File file) throws FileNotFoundException {

        FileOutputStream stream = new FileOutputStream(file);

        this.openFileOutput(file.getName(),Context.MODE_PRIVATE);
        //file.get

        String filename = "myfile";
        String fileContents = "Hello world!";

        byte[] bytes = fileContents.getBytes(Charsets.UTF_8);

        try (FileOutputStream fos = this.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    private void copy(File source, File destination) throws IOException {

        //destination.mkdirs();

        FileChannel in = new FileInputStream(source).getChannel();
        FileChannel out = new FileOutputStream(destination).getChannel();

        try {
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }
}
