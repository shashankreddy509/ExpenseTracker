import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.*
import platform.CoreGraphics.CGRectMake
import kotlinx.cinterop.ExperimentalForeignApi
import com.shashank.expense.tracker.common.App
import kotlinx.cinterop.CValue
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectGetWidth
import platform.CoreGraphics.CGRectGetHeight
import com.shashank.expense.tracker.di.initKoin

@OptIn(ExperimentalForeignApi::class)
fun MainViewController(): UIViewController {
    // Initialize Koin
    initKoin()
    
    val splashViewController = UIViewController()
    
    // Create and configure the main container view
    val containerView = UIView()
    containerView.setBackgroundColor(UIColor.whiteColor)
    splashViewController.setView(containerView)
    
    // Create and configure the logo image view
    val logoImageView = UIImageView()
    logoImageView.setImage(UIImage.imageNamed("ic_expenses"))
    logoImageView.setContentMode(UIViewContentMode.UIViewContentModeScaleAspectFit)
    val imageSize = 120.0 // Size in points
    
    // Get screen dimensions
    val bounds: CValue<CGRect> = UIScreen.mainScreen.bounds
    val screenWidth = CGRectGetWidth(bounds)
    val screenHeight = CGRectGetHeight(bounds)
    
    // Calculate center position
    val centerX = screenWidth * 0.5 - imageSize * 0.5
    val centerY = screenHeight * 0.5 - imageSize * 0.5 - 40.0
    
    logoImageView.setFrame(CGRectMake(
        centerX,
        centerY,
        imageSize,
        imageSize
    ))
    containerView.addSubview(logoImageView)
    
    // Create and configure the app name label
    val appNameLabel = UILabel()
    appNameLabel.setText("Expense Tracker")
    appNameLabel.setFont(UIFont.systemFontOfSize(24.0, 600.0)) // Semi-bold font
    appNameLabel.setTextColor(UIColor.blackColor)
    appNameLabel.setTextAlignment(NSTextAlignmentCenter)
    
    // Calculate label position
    val labelY = centerY + imageSize + 16.0
    
    appNameLabel.setFrame(CGRectMake(
        0.0,
        labelY,
        screenWidth,
        30.0
    ))
    containerView.addSubview(appNameLabel)
    
    // After a delay, transition to the main Compose UI
    platform.Foundation.NSTimer.scheduledTimerWithTimeInterval(
        2.0, // 2 second delay
        false,
        {
            val composeController = ComposeUIViewController { App() }
            splashViewController.presentViewController(
                composeController,
                true,
                null
            )
        }
    )
    
    return splashViewController
} 