package ipead.com.br.newandroidbancodepreco.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Classe para configuração do agendamento do serviço
 * @author Daniel Pereira
 */
public class Alarm {

    public static final long INTERVAL_3HOURS = 10800000;
    public static final long INTERVAL_HOUR = 3600000;
    public static final long INTERVAL_DAY = 86400000;
    public static final int TURNO1_SYNC = 10;
    public static final int TURNO2_SYNC = 17;
    public static final int TURNO1_SYNC_END = 13;
    public static final int TURNO2_SYNC_END = 20;
    public static final int NOTIFICATION_HOUR = 17;
    public static final int NOTIFICATION_MINUTE = 30;

    private Intent i;
    PendingIntent p;

    /**
     * Ativa o alarme de acordo com o intervalo e hora(no caso de ser INTERVAL_DAY)
     * @param context
     * @param interval
     * @param hourDay
     * @param intentAction
     * @param minute TODO
     */
    public void activateAlarm(Context context, long interval, int hourDay, String intentAction, int minute)
    {
        this.i = new Intent(intentAction);
        p = PendingIntent.getBroadcast(context, 0, i, 0);
        Calendar cal = Calendar.getInstance();

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (interval == Alarm.INTERVAL_DAY) {
            cal.set(Calendar.HOUR_OF_DAY, hourDay);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            if(cal.getTimeInMillis() < System.currentTimeMillis()){
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, p);
        Log.i("ALARMRECEIVER", "Alarme iniciado! interval: " + interval);
    }

    /**
     * Cancela o alarme definido de acordo com sua intentAction(Tipo do alarme como HOUR, DAY, etc)
     * @param context
     * @param intentAction
     */
    public void cancelAlarm(Context context, String intentAction)
    {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        i = new Intent(intentAction);
        p = PendingIntent.getBroadcast(context, 0, i, 0);
        alarm.cancel(p);
        Log.i("Alarm", "Alarme Cancelado");
    }

    /**
     * Retorna uma variavel do tipo int com a hora atual
     * @return
     */
    public int getHourNow()
    {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MINUTE);

    }

    /**
     * Método para verificar se é final de semana.
     *
     * @return
     */
    public static boolean isWeekend() {
        boolean isWeekend = false;

        final int dayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Log.i("INFORMACAO: ", "DIA: " + dayWeek);

        if (dayWeek == Calendar.SUNDAY || dayWeek == Calendar.SATURDAY) {
            isWeekend = true;
        }

        return isWeekend;
    }

    /**
     * Método para verificar se está no horário de servico.
     *
     * @return
     */
    public static boolean isScheduleWork() {
        boolean isSchedWork = false;

        final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Log.i("INFORMACAO: ", "Hora: " + hour);

        if (hour > 7 && hour < 18) {
            isSchedWork = true;
        }

        return isSchedWork;
    }
}
