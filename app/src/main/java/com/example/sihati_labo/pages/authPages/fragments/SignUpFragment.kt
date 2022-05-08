package com.example.sihati_labo.pages.authPages.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sihati_labo.databinding.FragmentSignUpBinding
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.example.sihati_labo.viewmodels.AuthViewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is signed in (non-null) and update UI accordingly.
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AuthViewModel::class.java]

        viewModel.userData.observe(requireActivity()) { firebaseUser ->
            if (firebaseUser != null) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
            }
        }

        binding.signup.setOnClickListener {
            if(binding.name.text.toString().isNotEmpty()
                &&binding.number.text.toString().isNotEmpty()
                &&binding.adress.text.toString().isNotEmpty()
                &&binding.email.text.toString().isNotEmpty()
                &&binding.password.text.toString().isNotEmpty()){
                viewModel.register(binding.email.text.toString().trim(),
                    binding.password.text.toString(),
                    binding.adress.text.toString(),
                    binding.name.text.toString(),
                    binding.number.text.toString(),
                    requireActivity())
            }else   Toast.makeText(requireActivity(),"fill your fields plz",Toast.LENGTH_SHORT).show()
        }
    }
}