package com.example.sihati_labo.pages.authPages.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.FragmentLoginBinding
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    private var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var progressDialog: ProgressDialog
    private var email=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is signed in (non-null) and update UI accordingly.
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AuthViewModel::class.java]

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("attendez, S'il vous plaît...")
        progressDialog.setCanceledOnTouchOutside(false)



        binding.login.setOnClickListener {
            if(binding.email.text.toString().isNotEmpty()
                &&binding.password.text.toString().isNotEmpty()){
                viewModel.signIn(binding.email.text.toString().trim(),binding.password.text.toString(),requireActivity())
            }else Toast.makeText(requireActivity(),"s'il vous plaît renseignez votre email et votre mot de passe",Toast.LENGTH_LONG).show()
        }

        binding.forgetPassword.setOnClickListener {
            setupEmailDialog()
        }
    }

    private fun setupEmailDialog(){
        val dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.email_bottom_sheet_content, null)
        view.findViewById<Button>(R.id.send).setOnClickListener {
            email = view.findViewById<TextInputEditText>(R.id.email).text.toString().trim()

            if(email.isEmpty()){
                Toast.makeText(requireContext(),"Entrez un e-mail...",Toast.LENGTH_LONG).show()
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(requireContext(),"Modèle d'e-mail invalide...",Toast.LENGTH_LONG).show()
            }
            else{
                recoverPassword(dialog)
            }
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun recoverPassword(dialog: BottomSheetDialog) {
        progressDialog.setMessage("Envoi de l'instruction de réinitialisation du mot de passe à $email")
        progressDialog.show()
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(),"Instructions envoyées à \n$email",Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(requireContext(),"Échec de l'envoi en raison de ${e.message}",Toast.LENGTH_LONG).show()
            }

    }
}