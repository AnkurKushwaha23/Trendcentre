package com.shop.shopstyle.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.shop.shopstyle.R
import com.shop.shopstyle.databinding.FragmentProfileBinding
import com.shop.shopstyle.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    companion object {
        private const val email = "ankursenpai@gmail.com"
        private const val mob_No = "+917048216866"
        private const val X_LINK = "https://twitter.com/AnkurKushwaha23"
        private const val LINKEDIN_LINK = "https://www.linkedin.com/in/ankur-kushwaha-818791248/"
        private const val GITHUB_LINK = "https://github.com/AnkurKushwaha23/"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email

        binding.txtUserEmail.text = email
        binding.imgBtnEditUser.setOnClickListener {
            val bottomSheet = UserDetailBottomSheet()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            (activity as? MainActivity)?.navigateToLogin()
        }

        binding.apply {

            btnQueries.setOnClickListener {
                getMail("TrendCentre: Queries")
            }

            btnReport.setOnClickListener {
                getMail("TrendCentre: Report Bug")
            }

            btnMail.setOnClickListener {
                getMail("TrendCentre: Suggestions")
            }


            imgWhatsapp.setOnClickListener {
                val sNumber = mob_No
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$sNumber")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = uri
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                Toast.makeText(requireContext(),"Thanks for Contacting Us !!",Toast.LENGTH_SHORT).show()
            }

            imgLink.setOnClickListener {
                openUrl(LINKEDIN_LINK)
            }

            imgX.setOnClickListener {
                openUrl(X_LINK)
            }

            imgGithub.setOnClickListener {
                openUrl(GITHUB_LINK)
            }
        }

        profileViewModel.userDetail.observe(viewLifecycleOwner, Observer { userDetail ->
            if (userDetail != null) {
                binding.apply {
                    txtUserName.visibility = View.VISIBLE
                    txtUserName.text = userDetail.name

                    txtUserMobileNo.visibility = View.VISIBLE
                    txtUserMobileNo.text = userDetail.mobNo.toString()

                    txtUserAddress.visibility = View.VISIBLE
                    txtUserAddress.text = userDetail.address

                    imgUser.setImageResource(
                        when (userDetail.gender) {
                            "Male" -> R.drawable.man // replace with actual drawable resource
                            "Female" -> R.drawable.woman // replace with actual drawable resource
                            else -> R.drawable.user
                        }
                    )
                }
            }
        })
    }

    private fun openUrl(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(url)
        )
        startActivity(intent)
        Toast.makeText(requireContext(), "Thanks for Contacting Us !!", Toast.LENGTH_SHORT).show()
    }

    private fun getMail(subject: String) {
        val uriBuilder = StringBuilder("mailto:" + Uri.encode(email))
        uriBuilder.append("?subject=" + Uri.encode(subject))
        val uriString = uriBuilder.toString()

        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriString))
        startActivity(Intent.createChooser(intent, "Send Suggestions"))
        Toast.makeText(requireContext(), "Thanks for Contacting Us !!", Toast.LENGTH_SHORT).show()
    }

}