package com.example.brainlypictureandroid

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*


var flag_start: Boolean = false
var effects_image = arrayOf(
    R.drawable.icon_search,
    R.drawable.icon_rainbow,
    R.drawable.icon_burst,
    R.drawable.icon_pulse,
    R.drawable.icon_radiation,
    R.drawable.icon_flame,
    R.drawable.icon_rainbow_vertical,
    R.drawable.icon_rainbow_propeler,
    R.drawable.icon_new_rainbow,
    R.drawable.icon_matrix,
    R.drawable.icon_march,
    R.drawable.icon_march2
    )
var time_sleep:Long = 100
var pic_auto_place = 0
var pic_auto_effect = arrayOf(0,0,0,0,0,0,0,0,0,0,0,0)
var wall_auto_place = 0
var wall_auto_effect = arrayOf(0,0,0,0,0,0,0,0,0,0,0,0)




class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connect()
    }

    //TODO: Make auto sending data(brightness and more)
    //TODO: Add warning
    //TODO: Make auto initial all empty labels
    //TODO: Check auto minutes
    //TODO: Add test auto mode
    //TODO: Add memory clear button


    fun connect() {
        val text_payload = textPayload
        val text_topic = textTopic

        val butt_settings = buttSettings
        val page_settings = pageSettings
        val text_connect = textConnect
        val seek_brightness = seekBrightness
        val toggle_auto = toggleAuto
        val butt_save = buttSave

        val butt_one_color = buttOneColor
        val page_one_color = pageOneColor
        val seek_red = seekRed
        val seek_blue = seekGreen
        val seek_green = seekBlue
        val spinner_one_color_mode = spinnerOneColorMode

        val page_effects = pagePicEffects
        val butt_effects = buttPicEffects
        val spinner_effects = spinnerPicEffects
        val image_effects = imageEffects
        val seek_speed =seekPicSpeed
        val seek_hsv = seekPicColorHSV
        val seek_bright = seekPicBrightness

        val page_wall_effects = pageEffectsWall
        val butt_wall_effects = buttWallEffects
        val spinner_wall_effects = spinnerWallEffects
        val image_wall_effects = imageWallEffects
        val seek_wall_speed =seekWallSpeed
        val seek_wall_hsv = seekWallColorHSV
        val seek_wall_bright = seekWallBrightness

        val page_pic_auto = pagePicAuto
        val butt_pic_auto = buttPicAuto
        val spinner_pic_auto_place = spinnerPlace
        val spinner_pic_auto_effect = spinnerEffectAuto
        val seek_pic_auto_speed =seekPicAutoSpeed
        val seek_pic_auto_hsv = seekPicAutoColorHSV
        val seek_pic_auto_bright = seekPicAutoBrightness
        val toggle_pic_auto = togglePicAuto
        val text_pic_auto_timer = textPicAutoTimer
        val seek_pic_auto_timer = seekPicAutoTimer
        val butt_pic_auto_start_time = buttPicAutoStartTime
        val butt_pic_auto_end_time = buttPicAutoEndTime


        val page_wall_auto = pageWallAuto
        val butt_wall_auto = buttWallAuto
        val spinner_wall_auto_place = spinnerWallPlace
        val spinner_wall_auto_effect = spinnerWallEffectAuto
        val seek_wall_auto_speed =seekWallAutoSpeed
        val seek_wall_auto_hsv = seekWallAutoColorHSV
        val seek_wall_auto_bright = seekWallAutoBrightness
        val toggle_wall_auto = toggleWallAuto
        val text_wall_auto_timer = textWallAutoTimer
        val seek_wall_auto_timer = seekWallAutoTimer
        val butt_wall_auto_start_time = buttWallAutoStartTime
        val butt_wall_auto_end_time = buttWallAutoEndTime

        val Memory = getSharedPreferences("main_memory", Context.MODE_PRIVATE)
        val saver = Memory.edit();

        val clientId = MqttClient.generateClientId()
        val client = MqttAndroidClient(
            this.applicationContext, "tcp://192.168.1.112:1883",
            clientId
        )


        val options = MqttConnectOptions()
        options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1
        options.isCleanSession = false
        options.userName = "enes"
        options.password = "1234".toCharArray()



        try {
            val token = client.connect(options)
            //IMqttToken token = client.connect();
            token.actionCallback = object : IMqttActionListener {
                @SuppressLint("SimpleDateFormat")
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // We are connected
                    Log.d("file", "onSuccess")

                    subscribe(client, "picture/connect")
                    subscribe(client, "picture/info")
                    subscribe(client, "picture/pic/effect/speed")
                    subscribe(client, "picture/wall/effect/speed")
                    subscribe(client, "picture/pic/effect_one_color")
                    subscribe(client, "picture/pic/red")
                    subscribe(client, "picture/pic/green")
                    subscribe(client, "picture/pic/blue")
                    subscribe(client, "picture/time")
                    subscribe(client, "picture/pic/auto/start/hour")
                    subscribe(client, "picture/pic/auto/start/min")
                    subscribe(client, "picture/pic/auto/end/hour")
                    subscribe(client, "picture/pic/auto/end/min")



                    //publish(client, "update", "water/info")

                    if(!flag_start){
                        seek_brightness.progress = Memory.getInt("brightness",0)
                        toggle_auto.isChecked = Memory.getBoolean("bool_auto", false)

                        seek_bright.progress = Memory.getInt("pic_bright",0)
                        seek_speed.progress = Memory.getInt("pic_speed",0)
                        seek_hsv.progress = Memory.getInt("pic_hsv",0)

                        seek_wall_bright.progress = Memory.getInt("wall_bright",0)
                        seek_wall_speed.progress = Memory.getInt("wall_speed",0)
                        seek_wall_hsv.progress = Memory.getInt("wall_hsv",0)

                        toggle_pic_auto.isChecked = Memory.getBoolean("bool_pic_auto", false)
                        seek_bright.progress = Memory.getInt("pic_bright",0)
                        seek_speed.progress = Memory.getInt("pic_speed",0)
                        seek_hsv.progress = Memory.getInt("pic_hsv",0)

                        toggle_wall_auto.isChecked = Memory.getBoolean("bool_wall_auto", false)
                        seek_wall_auto_bright.progress = Memory.getInt("wall_bright",0)
                        seek_wall_hsv.progress = Memory.getInt("wall_hsv",0)
                        seek_wall_auto_speed.progress = Memory.getInt("wall_speed",0)

                        page_settings.visibility = View.GONE
                        page_one_color.visibility = View.GONE
                        page_effects.visibility = View.GONE
                        page_wall_effects.visibility = View.GONE
                        page_pic_auto.visibility = View.GONE
                        page_wall_auto.visibility = View.GONE
                        flag_start = true
                    }



                    // SETTINGS PAGE
                    butt_settings.setOnClickListener(View.OnClickListener {
                        if(!page_settings.isVisible) {
                            page_settings.visibility = View.VISIBLE
                            publish(client, "info", "picture/connect")
                            Thread.sleep(time_sleep)
                            publish(client, seek_brightness.progress.toString(), "picture/brightness")
                            Thread.sleep(time_sleep)
                            if(toggle_auto.isChecked){
                                publish(client, "on", "picture/auto")
                                toggle_auto.setBackgroundColor(Color.GREEN)
                                toggle_auto.text = "ON"
                            }
                            else{
                                publish(client, "off", "picture/auto")
                                toggle_auto.setBackgroundColor(Color.RED)
                                toggle_auto.text = "OFF"
                            }
                        }
                        else{
                            page_settings.visibility = View.GONE
                        }
                    })

                    toggle_auto.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{ _, isChecked ->
                        if(isChecked){
                            publish(client, "on", "picture/auto")
                            toggle_auto.setBackgroundColor(Color.GREEN)
                            toggle_auto.text = "ON"
                        }
                        else{
                            publish(client, "off", "picture/auto")
                            toggle_auto.setBackgroundColor(Color.RED)
                            toggle_auto.text = "OFF"
                        }
                    })

                    seek_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/brightness")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })

                    butt_save.setOnClickListener(View.OnClickListener {
                        publish(client, "save", "picture/save")
                        butt_save.setBackgroundColor(Color.GREEN)
                        saver.putInt("brightness", seek_brightness.progress)
                        saver.putBoolean("bool_auto", toggle_auto.isChecked)

                        saver.putInt("pic_bright", seek_bright.progress)
                        saver.putInt("pic_speed", seek_speed.progress)
                        saver.putInt("pic_hsv", seek_hsv.progress)

                        saver.putInt("wall_bright", seek_wall_bright.progress)
                        saver.putInt("wall_speed", seek_wall_speed.progress)
                        saver.putInt("wall_hsv", seek_wall_hsv.progress)

                        saver.putBoolean("bool_pic_auto", toggle_pic_auto.isChecked)
                        saver.putInt("pic_bright", seek_pic_auto_bright.progress)
                        saver.putInt("pic_hsv", seek_pic_auto_hsv.progress)
                        saver.putInt("pic_speed", seek_pic_auto_speed.progress)

                        saver.putBoolean("bool_wall_auto", toggle_wall_auto.isChecked)
                        saver.putInt("wall_bright", seek_wall_auto_bright.progress)
                        saver.putInt("wall_hsv", seek_wall_auto_hsv.progress)
                        saver.putInt("wall_speed", seek_wall_auto_speed.progress)
                        saver.apply()
                    })


                    // ONE COLOR PAGE
                    butt_one_color.setOnClickListener(View.OnClickListener {
                        if(!page_one_color.isVisible) {
                            page_one_color.visibility = View.VISIBLE
                            publish(client, "30", "picture/pic/effect")
                        }
                        else{
                            page_one_color.visibility = View.GONE
                        }
                    })

                    seek_red.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/red")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })

                    seek_green.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/green")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })

                    seek_blue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/blue")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    colorPicker.setColorListener(ColorEnvelopeListener { envelope, _ ->
                        linearLayout.setBackgroundColor(envelope.color)
                        var colors = envelope.argb
                        seek_red.progress = colors[1]
                        seek_green.progress = colors[2]
                        seek_blue.progress = colors[3]
                    })


                    spinner_one_color_mode.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            itemSelected: View, selectedItemPosition: Int, selectedId: Long) {

                            publish(client, selectedItemPosition.toString(), "picture/effect_one_color")

                        }
                        override fun onNothingSelected(arg0: AdapterView<*>) {

                        }
                    })



                    // PIC EFFECTS PAGE
                    butt_effects.setOnClickListener(View.OnClickListener {
                        if(!page_effects.isVisible) {
                            page_effects.visibility = View.VISIBLE
                            publish(client, seek_bright.progress.toString(), "picture/pic/effect/brightness")
                            Thread.sleep(time_sleep)
                            publish(client, seek_hsv.progress.toString(), "picture/pic/effect/color")
                            Thread.sleep(time_sleep)
                            publish(client, seek_speed.progress.toString(), "picture/pic/effect/speed")
                        }
                        else{
                            page_effects.visibility = View.GONE
                        }
                    })


                    spinner_effects.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            itemSelected: View, selectedItemPosition: Int, selectedId: Long) {

                            publish(client, selectedItemPosition.toString(), "picture/pic/effect")

                            image_effects.setImageResource(effects_image[selectedItemPosition]);
                            }
                        override fun onNothingSelected(arg0: AdapterView<*>) {

                        }
                    })


                    seek_speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/effect/speed")
                            seek_wall_auto_speed.progress = progress
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_hsv.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/pic/effect/color")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_bright.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/pic/effect/brightness")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })



                    // EFFECTS WALL PAGE
                    butt_wall_effects.setOnClickListener(View.OnClickListener {
                        if(!page_wall_effects.isVisible) {
                            page_wall_effects.visibility = View.VISIBLE
                            publish(client, seek_wall_bright.progress.toString(), "picture/wall/effect/brightness")
                            Thread.sleep(time_sleep)
                            publish(client, seek_wall_hsv.progress.toString(), "picture/wall/effect/color")
                            Thread.sleep(time_sleep)
                            publish(client, seek_wall_speed.progress.toString(), "picture/effect/speed")
                        }
                        else{
                            page_wall_effects.visibility = View.GONE
                        }
                    })


                    spinner_wall_effects.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            itemSelected: View, selectedItemPosition: Int, selectedId: Long) {

                            publish(client, selectedItemPosition.toString(), "picture/wall/effect")

                            image_wall_effects.setImageResource(effects_image[selectedItemPosition]);
                        }
                        override fun onNothingSelected(arg0: AdapterView<*>) {

                        }
                    })


                    seek_wall_speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/effect/speed")
                            seek_pic_auto_speed.progress = progress
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_wall_hsv.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/wall/effect/color")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_wall_bright.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/wall/effect/brightness")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })



                    // AUTO PIC PAGE
                    butt_pic_auto.setOnClickListener(View.OnClickListener {
                        if(!page_pic_auto.isVisible) {
                            page_pic_auto.visibility = View.VISIBLE
                        }
                        else{
                            page_pic_auto.visibility = View.GONE
                        }
                    })


                    spinner_pic_auto_place.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                            publish(client, selectedItemPosition.toString(), "picture/pic/auto/place")
                        }
                        override fun onNothingSelected(arg0: AdapterView<*>) {

                        }
                    })

                    spinner_pic_auto_effect.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                            publish(client, selectedItemPosition.toString(), "picture/pic/auto/effect")
                        }
                        override fun onNothingSelected(arg0: AdapterView<*>) {

                        }
                    })

                    seek_pic_auto_speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/effect/speed")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_pic_auto_hsv.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/pic/effect/color")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_pic_auto_bright.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/pic/effect/brightness")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    toggle_pic_auto.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{ _, isChecked ->
                        if(isChecked){
                            publish(client, "on", "picture/pic/auto")
                            toggle_pic_auto.setBackgroundColor(Color.GREEN)
                            toggle_pic_auto.text = "ON"
                        }
                        else{
                            publish(client, "off", "picture/pic/auto")
                            toggle_pic_auto.setBackgroundColor(Color.RED)
                            toggle_pic_auto.text = "OFF"
                        }
                    })


                    seek_pic_auto_timer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/pic/auto/timer")
                            text_pic_auto_timer.text = progress.toString()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    butt_pic_auto_start_time.setOnClickListener {
                        val cal = Calendar.getInstance()
                        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            butt_pic_auto_start_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            butt_wall_auto_start_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            publish(client, hour.toString(), "picture/auto/start/hour")
                            publish(client, hour.toString(), "picture/auto/start/min")
                        }
                        TimePickerDialog(this@MainActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                    }

                    butt_pic_auto_end_time.setOnClickListener {
                        val cal = Calendar.getInstance()
                        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            butt_pic_auto_end_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            butt_wall_auto_end_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            publish(client, hour.toString(), "picture/auto/end/hour")
                            publish(client, hour.toString(), "picture/auto/end/min")
                        }
                        TimePickerDialog(this@MainActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                    }







                    // AUTO WALL PAGE
                    butt_wall_auto.setOnClickListener(View.OnClickListener {
                        if(!page_wall_auto.isVisible) {
                            page_wall_auto.visibility = View.VISIBLE
                        }
                        else{
                            page_wall_auto.visibility = View.GONE
                        }
                    })


                    spinner_wall_auto_place.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            itemSelected: View, selectedItemPosition: Int, selectedId: Long) {

                            publish(client, selectedItemPosition.toString(), "picture/wall/auto/place")
                        }
                        override fun onNothingSelected(arg0: AdapterView<*>) {

                        }
                    })

                    spinner_wall_auto_effect.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            itemSelected: View, selectedItemPosition: Int, selectedId: Long) {

                            publish(client, selectedItemPosition.toString(), "picture/wall/auto/effect")
                        }
                        override fun onNothingSelected(arg0: AdapterView<*>) {

                        }
                    })

                    seek_wall_auto_speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/effect/speed")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_wall_auto_hsv.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/wall/effect/color")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    seek_wall_auto_bright.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/wall/effect/brightness")
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    toggle_wall_auto.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{ _, isChecked ->
                        if(isChecked){
                            publish(client, "on", "picture/wall/auto")
                            toggle_wall_auto.setBackgroundColor(Color.GREEN)
                            toggle_wall_auto.text = "ON"
                        }
                        else{
                            publish(client, "off", "picture/wall/auto")
                            toggle_wall_auto.setBackgroundColor(Color.RED)
                            toggle_wall_auto.text = "OFF"
                        }
                    })


                    seek_wall_auto_timer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            publish(client, progress.toString(), "picture/wall/auto/timer")
                            text_wall_auto_timer.text = progress.toString()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        }
                    })


                    butt_wall_auto_start_time.setOnClickListener {
                        val cal = Calendar.getInstance()
                        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            butt_wall_auto_start_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            butt_pic_auto_start_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            publish(client, hour.toString(), "picture/auto/start/hour")
                            publish(client, hour.toString(), "picture/auto/start/min")
                        }
                        TimePickerDialog(this@MainActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                    }

                    butt_wall_auto_end_time.setOnClickListener {
                        val cal = Calendar.getInstance()
                        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                            cal.set(Calendar.HOUR_OF_DAY, hour)
                            cal.set(Calendar.MINUTE, minute)
                            butt_wall_auto_end_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            butt_pic_auto_end_time.text = SimpleDateFormat("HH:mm").format(cal.time)
                            publish(client, hour.toString(), "picture/auto/end/hour")
                            publish(client, hour.toString(), "picture/auto/end/min")
                        }
                        TimePickerDialog(this@MainActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                    }






                    client.setCallback(object : MqttCallback {

                        override fun connectionLost(cause: Throwable) {
                        }
                        @Throws(Exception::class)
                        override fun messageArrived(topic: String, message: MqttMessage) {
                            Log.d("file", message.toString())


                            if(topic == "picture/connect") {
                                if(message.toString() == "connected"){
                                    text_connect.setBackgroundColor(Color.GREEN)
                                    text_connect.text = "Connected"
                                }
                                else{
                                    text_connect.setBackgroundColor(Color.RED)
                                    text_connect.text = "Disconnected"
                                }
                            }


                            text_payload.text = message.toString()
                            text_topic.text = topic
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken) {

                        }
                    })
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("file", "onFailure")

                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }




    fun publish(client: MqttAndroidClient, payload: String, topic: String) {
        var encodedPayload: ByteArray
        try {
            encodedPayload = payload.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            client.publish(topic, message)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }



    fun subscribe(client: MqttAndroidClient, topic: String) {
        val qos = 1
        try {
            val subToken = client.subscribe(topic, qos)
            subToken.actionCallback = object : IMqttActionListener {

                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // The message was published
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }
}
