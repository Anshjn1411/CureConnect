package login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.ViewModelProvider

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.project.cureconnect.R
import com.project.cureconnect.login.LoginUiState
import com.project.cureconnect.login.UserModel
import com.project.cureconnect.pateints.navigationRoutes.Screen
import com.project.ecommerceLocal.utils.Apputils


@Composable
fun SignIn(navController: NavController, authViewModel: AuthViewModel = viewModel(),
           onLoginSuccess: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var userInput by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userData by remember { mutableStateOf<UserModel?>(null) }






    // Initialize Google Sign-In
    authViewModel.initGoogleSignIn(context)

    // Create a launcher for Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                isLoading = true
                authViewModel.handleGoogleSignInResult(result) { success, errorMessage ->
                    isLoading = false
                    if (success) {
                        onLoginSuccess()
                        // Navigate directly to MainDashboard on successful Google sign-in
                        navController.navigate(Screen.MainDashBoard.routes) {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        Apputils.showToast(context, errorMessage ?: "Google Sign-In Failed")
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(top=80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(painter = painterResource(R.drawable.cureconnect_logo), contentDescription = null)
        Spacer(Modifier.height(10.dp))
        Text(text = "Please Sign in to continue",
            fontWeight = Bold,
            color = colorResource(R.color.primary_blue_light)
        )
        Spacer(Modifier.height(40.dp))
        OutlinedTextField(value = userInput, onValueChange = {
            userInput = it
        },
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.user_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Email, Username, or Phone", color = colorResource(R.color.black), fontStyle = FontStyle.Italic)
            }
            }
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(value = password, onValueChange = {
            password = it
        },
            modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
            placeholder = { Row {
                Image(painter = painterResource(R.drawable.pass_logo), contentDescription = null)
                Spacer(Modifier.width(10.dp))

                Text("Your Password", color = Color.Black, fontStyle = FontStyle.Italic)
            }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = {
            isLoading = true
            authViewModel.login(userInput, password) { success, errormessage ->
                if (success) {
                    onLoginSuccess()
                    isLoading = false



                } else {
                    isLoading = false
                    Apputils.showToast(context, errormessage ?: "Error in login")
                }
            }
        },
            modifier = Modifier.padding(top=30.dp).size(350.dp, 65.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_blue_light)),
            shape = RoundedCornerShape(10.dp)) {
            if(isLoading){
                CircularProgressIndicator()
            } else {
                Text(text = "Sign In",
                    fontSize = 30.sp,
                    fontWeight = Bold,
                    color = Color.White,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(text = "---Or Sign in with social account---",
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(12.dp))
                    .clickable { /* Apple login implementation */ }
                    .border(2.dp, colorResource(R.color.primary_blue_light), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.apple_logo), contentDescription = null,
                    modifier = Modifier.size(250.dp))
            }
            Spacer(Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(12.dp))
                    .clickable {
                        // Launch Google Sign-In
                        googleSignInLauncher.launch(authViewModel.getGoogleSignInIntent())
                    }
                    .border(2.dp, colorResource(R.color.primary_blue_light), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.google_logo), contentDescription = null,
                    modifier = Modifier.size(250.dp))
            }
            Spacer(Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(12.dp))
                    .clickable { /* Facebook login implementation */ }
                    .border(2.dp, colorResource(R.color.primary_blue), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.facebook_logo), contentDescription = null,
                    modifier = Modifier.size(250.dp))
            }
        }
        Spacer(Modifier.height(30.dp))
        Text(
            text = "Forgot your password?",
            color = colorResource(R.color.primary_blue),
            fontSize = 16.sp,
            fontWeight = Bold,
            modifier = Modifier.clickable {
                navController.navigate(Screen.ForgotPassword.routes)
            }
        )
        Spacer(Modifier.height(15.dp))
        Row {
            Text(
                text = "Didn't have an account?",
                color = colorResource(R.color.primary_blue_light),
                fontSize = 16.sp,
            )
            Text(
                text = "Sign Up Here",
                color = colorResource(R.color.primary_blue_light),
                fontSize = 16.sp,
                fontWeight = Bold,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable {
                    navController.navigate(Screen.SignUp.routes)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun tingPreview() {
    SignIn(rememberNavController(), onLoginSuccess = {})
}