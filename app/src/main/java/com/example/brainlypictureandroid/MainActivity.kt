package com.example.brainlypictureandroid

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.io.UnsupportedEncodingException

var flag_start = false
var flag_page_settings = false



class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connect()
    }




    fun connect() {
        val text_payload = textPayload
        val text_topic = textTopic

        val butt_settings = buttSettings
        val page_settings = pageSettings
        val text_connect = textConnect

        val butt_one_color = buttOneColor
        val page_one_color = pageOneColor
        val seek_red = seekRed
        val seek_green = seekGreen
        val seek_blue = seekBlue


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
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // We are connected
                    Log.d("file", "onSuccess")

                    subscribe(client, "picture/connect")
                    subscribe(client, "picture/red")



                    //publish(client, "update", "water/info")

                    if(!flag_start){
                        publish(client, "info", "picture/connect")
                        page_settings.visibility = View.GONE
                        page_one_color.visibility = View.GONE
                        flag_start = true
                    }


                    butt_settings.setOnClickListener(View.OnClickListener {
                        if(!page_settings.isVisible) {
                            page_settings.visibility = View.VISIBLE
                        }
                        else{
                            page_settings.visibility = View.GONE
                        }
                    })


                    butt_one_color.setOnClickListener(View.OnClickListener {
                        if(!page_one_color.isVisible) {
                            page_one_color.visibility = View.VISIBLE
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
                            text_topic.text = topic.toString()
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
        var encodedPayload = ByteArray(0)
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
