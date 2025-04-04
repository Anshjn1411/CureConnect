package login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.project.cureconnect.R


@Composable
fun ForgotPassword(navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize().padding(top=80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(painter = painterResource(R.drawable.cureconnect_logo), contentDescription = null)
        Spacer(Modifier.height(10.dp))
        Text(text = "Forgot your password?",
            fontWeight = Bold,
            color = colorResource(R.color.primary_blue)
        )
        Spacer(Modifier.height(40.dp))

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = "" , onValueChange = {},
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.mail_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Your E-mail" , color = Color.Black , fontStyle = FontStyle.Italic)

            }
            }
        )

        Spacer(Modifier.height(15.dp))
        Text("* We will send you a message to set or reset your new password",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            fontSize = 15.sp,
        )
        Button(onClick = { navController.navigateUp() } ,
            modifier = Modifier.padding(top =30.dp).size(350.dp,65.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_blue_light)),
            shape = RoundedCornerShape(10.dp) ){
            Text(text = "Submit",
                fontSize = 30.sp,
                fontWeight = Bold,
                color = Color.White,

                )
        }


    }
}
//@Preview(showBackground = true)
//@Composable
//fun ForgotPasswordPreview() {
//    ForgotPassword()
//}
