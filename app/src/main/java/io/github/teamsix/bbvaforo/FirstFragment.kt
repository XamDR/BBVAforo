package io.github.teamsix.bbvaforo

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.github.teamsix.bbvaforo.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

	private var _binding: FragmentFirstBinding? = null
	private val binding get() = _binding!!

	override fun onAttach(context: Context) {
		super.onAttach(context)
		(context as MainActivity).setOnSuccessListener { uri ->
			processImage(uri)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentFirstBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun processImage(uri: Uri) {
		val input = InputImage.fromFilePath(requireContext(), uri)
		detectFaces(input)
	}

	@SuppressLint("SetTextI18n")
	private fun detectFaces(input: InputImage) {
		// Set options
		val options = FaceDetectorOptions.Builder()
			.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
			.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
			.setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
			.enableTracking()
			.setMinFaceSize(0.15f)
			.build()

		val detector = FaceDetection.getClient(options)

		// Start recognition process
		detector.process(input).addOnSuccessListener { faces ->
			for (face in faces) {
				android.util.Log.d("SUCCESS", "DEMO")
				binding.faces.text = faces.size.toString() + " caras"
				if (face.smilingProbability != null) {
					val smileProb = face.smilingProbability!! * 100
					android.util.Log.d("DEMO", smileProb.toString())
				}
			}
		}.addOnFailureListener { e ->
			Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
		}
	}
}