package woo.Daykey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 리셋할것인지 안 할 것인지 물어보는 플레그먼트
 */

public class ResetFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("초기화")
                .setMessage("정말로 초기화 하시겠습니까?")
                .setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SettingPreferences set = new SettingPreferences(getActivity());
                        set.saveInt("db_version", -1);
                        set.saveBoolean("firstStart", true);
                        Toast.makeText(getActivity(), "앱을 다시 시작하면 데이터를 가져옵니다", Toast.LENGTH_LONG).show();
                        getActivity().moveTaskToBack(true);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "취소 되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
