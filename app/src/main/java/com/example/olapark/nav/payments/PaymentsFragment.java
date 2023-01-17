package com.example.olapark.nav.payments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.olapark.R;
import com.example.olapark.databinding.FragmentPaymentsBinding;

public class PaymentsFragment extends Fragment {
    private FragmentPaymentsBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentPaymentsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        SharedPreferences sp = this.getActivity().getSharedPreferences("auto-login", MODE_PRIVATE);
        if (sp.contains("balance")){
            float balance = (float) sp.getFloat("balance", 0);

            TextView balance_amount = root.findViewById(R.id.balance_amount);
            balance_amount.setText(String.valueOf(balance));
        }

        depositMoney();

        raiseMoney();

        checkPaymentHistory();

        return root;
    }

    private void checkPaymentHistory() {

        ImageView check_payment_history = root.findViewById(R.id.check_payment_history_form);

        check_payment_history.setOnClickListener(v -> {

            ConstraintLayout cl = root.findViewById(R.id.fragment_payments);
            cl.removeAllViewsInLayout();

            getFragmentManager().beginTransaction().replace(R.id.fragment_payments, new CheckPaymentHistoryFragment(),"Check Payment History").commit();

        });

    }

    private void raiseMoney() {

        ImageView raise_money = root.findViewById(R.id.raise_money_form);

        raise_money.setOnClickListener(v -> {

            ConstraintLayout cl = root.findViewById(R.id.fragment_payments);
            cl.removeAllViewsInLayout();

            getFragmentManager().beginTransaction().replace(R.id.fragment_payments, new RaiseMoneyFragment(),"Raise Money").commit();

        });

    }

    private void depositMoney() {

        ImageView deposit_money = root.findViewById(R.id.deposit_money_form);

        deposit_money.setOnClickListener(v -> {

            ConstraintLayout cl = root.findViewById(R.id.fragment_payments);
            cl.removeAllViewsInLayout();

            getFragmentManager().beginTransaction().replace(R.id.fragment_payments, new DepositMoneyFragment(),"Deposit Money").commit();

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}