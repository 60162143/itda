package com.example.itda.library.mail;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.itda.BuildConfig;
import com.example.itda.R;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import io.github.muddz.styleabletoast.StyleableToast;

public class SendMail extends AppCompatActivity {
    String user = BuildConfig.MAIL_EMAIL; // 보내는 계정의 id
    String password = BuildConfig.MAIL_PASSWORD; // 보내는 계정의 pw ( 계정 비밀번호가 아닌 Google 앱 비밀번호! )

    GmailSender gMailSender = new GmailSender(user, password);
    String emailCode = gMailSender.getEmailCode();
    public String sendSecurityCode(Context context, String sendTo) {
        try {
            gMailSender.sendMail("[Itda] 이메일 인증을 위한 인증번호를 안내 드립니다.", "인증번호 : " + emailCode, sendTo);
            //StyleableToast.makeText(context, "인증번호가 전송되었습니다.", R.style.blueToast).show();
            Log.d("mailTransferComplete", "메일 전송 완료...");
            //Toast.makeText(this,"인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
            return emailCode;
        } catch (SendFailedException e) {
            //Toast.makeText(this,"이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            StyleableToast.makeText(context, "이메일 형식이 잘못되었습니다.", R.style.redToast).show();
        } catch (MessagingException e) {
            //Toast.makeText(this,"인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            StyleableToast.makeText(context, "인터넷 연결을 확인해주세요.", R.style.redToast).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
