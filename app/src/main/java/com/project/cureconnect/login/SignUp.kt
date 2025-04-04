package login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.project.cureconnect.R
import com.project.cureconnect.pateints.navigationRoutes.Screen
import com.project.ecommerceLocal.utils.Apputils

@Composable
fun SignUp(navController: NavController, authViewModel: AuthViewModel = viewModel()){
    var isValid by remember { mutableStateOf(true) }
    val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var confirmpass by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Patient") }

    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().padding(top=80.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {

        Image(painter = painterResource(R.drawable.cureconnect_logo), contentDescription = null)
        Spacer(Modifier.height(10.dp))
        Text(text = "Please Sign up to get new account",
            fontWeight = Bold,
            color = colorResource(R.color.primary_blue_light)
        )
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(value = name , onValueChange = {name=it},
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.user_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Your Name" , color = Color.Black , fontStyle = FontStyle.Italic)

            }
            }

        )
        Spacer(Modifier.height(15.dp))
        // Role selection (Doctor/Patient)

        OutlinedTextField(value = email , onValueChange = {email=it},
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.mail_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Your E-mail" , color = Color.Black , fontStyle = FontStyle.Italic)

            }
            }
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = phone , onValueChange = {phonenumber->
            if(phonenumber.all { it.isDigit() }){
                phone=phonenumber
            }

        },
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.phone_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Your Phone Number" , color = Color.Black , fontStyle = FontStyle.Italic)

            }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = password ,  onValueChange = { newText ->
            password = newText
            isValid = passwordRegex.matches(newText)
        },
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.pass_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Your Password" , color = Color.Black , fontStyle = FontStyle.Italic)

            }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = confirmpass ,onValueChange = { newText ->
            confirmpass = newText
            isValid = passwordRegex.matches(newText)
        },
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.pass_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Confirm Your Password" , color = Color.Black , fontStyle = FontStyle.Italic)

            }
            }
        )
        Spacer(Modifier.height(15.dp))
        Text("Select your role:", fontWeight = Bold, fontSize = 18.sp, color = Color.Black)
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            RadioButton(selected = selectedRole == "Doctor", onClick = { selectedRole = "Doctor" })
            Text("Doctor", modifier = Modifier.clickable { selectedRole = "Doctor" })
            Spacer(Modifier.width(20.dp))
            RadioButton(selected = selectedRole == "Patient", onClick = { selectedRole = "Patient" })
            Text("Patient", modifier = Modifier.clickable { selectedRole = "Patient" })
        }
        Spacer(Modifier.height(10.dp))


        Text("By clicking the Sign Up Button , you agree with the public offer.",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            fontSize = 15.sp,

            )
        Button(onClick = {
            if(confirmpass==password){
                isLoading=true
                authViewModel.signup(email,password, name , phone , selectedRole ){success , errormessage ->
                    if(success){
                        isLoading=false
                        navController.navigate(Screen.SignIn.routes){
                            popUpTo("auth"){inclusive= true}
                        }

                    }else{
                        isLoading=false
                        Apputils.showToast(context,errormessage?:"Error in signup")

                    }
                }
            }else{
                Apputils.showToast(context,"Password must be same")
            }
        } ,
            enabled = !isLoading,

            modifier = Modifier.padding(top =30.dp).size(350.dp,65.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_blue_light)),
            shape = RoundedCornerShape(10.dp) ){
            if(isLoading){
                CircularProgressIndicator()
            }else{
                Text(text = "Sign Up",
                    fontSize = 30.sp,
                    fontWeight = Bold,
                    color = Color.White,

                    )
            }

        }

        Spacer(Modifier.height(30.dp))


        Row {
            Text(
                text = "Already have an account?",
                color = colorResource(R.color.primary_blue),
                fontSize = 16.sp,

                )
            Text(
                text = "Sign In Here",
                color = colorResource(R.color.primary_blue_light),
                fontSize = 16.sp,
                fontWeight = Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable { navController.navigate(Screen.SignIn.routes) }
            )

        }





    }

}
//
//@Preview(showBackground = true)
//@Composable
//fun SignUpPreview() {
//    SignUp()
//}
