package br.com.seocursos.seocursos.Outros;

import android.app.ProgressDialog;
import android.content.Context;

import br.com.seocursos.seocursos.R;

/**
 * Created by Aluno on 19/04/2018.
 */

public class ProgressDialogHelper {
    private ProgressDialog pd;
    private Context context;

    public ProgressDialogHelper(Context context){
        this.context = context;
        buildProgressDialog();
    }

    private void buildProgressDialog(){
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(context.getResources().getString(R.string.aguarde));
        pd.setCancelable(false);
    }
    public void open(){
        if(!pd.isShowing()) {
            pd.show();
        }
    }
    public void close(){
        if(pd.isShowing()) {
            pd.dismiss();
        }
    }
}
