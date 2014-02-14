using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Net;
using System.Diagnostics;
using System.Runtime.InteropServices;

namespace PCQuickCommandsClient
{
	
	public partial class Form1 : Form
	{
		[DllImport("user32.dll", SetLastError = true)] static extern bool LockWorkStation();
		String usname;
		String CommandPath;
		String previousCommand = "NOTHING";
		String execCommand;
		public Form1()
		{
			InitializeComponent();
		}

		private void notifyIcon1_MouseDoubleClick(object sender, MouseEventArgs e)
		{
			this.WindowState = FormWindowState.Normal;
			this.ShowInTaskbar = true;
			this.Show();
			this.Visible = true;
			this.BringToFront();
			this.Activate();
		}

		private void Form1_Load(object sender, EventArgs e)
		{
			try
			{
				usname = File.ReadAllText(Environment.CurrentDirectory + "/" + "username.un");
			}
			catch (IOException ex) {
				File.WriteAllText(Environment.CurrentDirectory + "/" + "username.un", "default");
			}

			
			if (usname == "default") {

				this.WindowState = FormWindowState.Minimized;
				this.ShowInTaskbar = false;
				this.Hide();
				this.Visible = false;

				Form2 frm2 = new Form2();
				frm2.Show();
				frm2.Activate();
			}
			else
			{
				// So that we dont execute the previous command again on startup
				try
				{
					previousCommand = File.ReadAllText(Environment.CurrentDirectory + "/" + "previouscommand.pc");
				}
				catch (IOException ex)
				{
					File.WriteAllText(Environment.CurrentDirectory + "/" + "previouscommand.pc", "NOTHING");
				}
				
				timer1.Enabled = true;
			}
			
		}

		private void button1_Click(object sender, EventArgs e)
		{
			this.Hide();
		}

		private void timer1_Tick(object sender, EventArgs e)
		{
			try {
				CommandPath = Path.Combine(Environment.CurrentDirectory, "command.txt");
				String path = "http://serverpath.com/PCQuickCommands/" + usname + "/" + "command.txt";
				execCommand = (new WebClient()).DownloadString(path);
				// So that we dont execute same command twice
				bool isPrevExecCmd = execCommand.Equals(previousCommand);

				if (isPrevExecCmd == false)
				{
					timer1.Enabled = false;
					ExecuteCommand(execCommand);
					timer1.Enabled = true;
					previousCommand = execCommand;
					File.WriteAllText(Environment.CurrentDirectory + "/" + "previouscommand.pc", previousCommand);
				}
			}

			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString());
			}

		}

		public void ExecuteCommand(String command)
		{
			switch (command) { 
			case "HIBERNATE":
				Application.SetSuspendState(PowerState.Hibernate, true, true);
				notifyIcon1.ShowBalloonTip(1000, "Hibernate", "Hibernating computer now", ToolTipIcon.Info);
				break;
			case "SLEEP":
				Application.SetSuspendState(PowerState.Suspend, true, true);
				notifyIcon1.ShowBalloonTip(1000, "Sleep", "Putting computer to sleep now", ToolTipIcon.Info);
				break;
			case "SHUTDOWN":
				notifyIcon1.ShowBalloonTip(1000, "Shutdown", "Shutting down computer now", ToolTipIcon.Info);
				Process.Start("cmd", "/c shutdown /s /t 1 /f");
				break;
			case "RESTART":
				notifyIcon1.ShowBalloonTip(1000, "Restart", "Restarting computer now", ToolTipIcon.Info);
				Process.Start("cmd", "/c shutdown /r /t 1 /f");
				break;
			case "LOGOFF":
				notifyIcon1.ShowBalloonTip(1000, "Shutdown", "Logging off computer now", ToolTipIcon.Info);
				Process.Start("cmd", "/c shutdown /l /t 1 /f");
				break;
			case "LOCK":
				notifyIcon1.ShowBalloonTip(1000, "Lock", "Locking computer now", ToolTipIcon.Info);
				bool result = LockWorkStation();
				break;
			case "DEFAULT":
				notifyIcon1.ShowBalloonTip(500, "Test", "Test command received, ignore", ToolTipIcon.Info);
				break;
			}

			if (command.Contains("RUN")) {
				string exPath = command.Remove(0, 3);
				Process.Start("cmd", "/C " + exPath);
				notifyIcon1.ShowBalloonTip(1000, "Run", "Run command received, executing now", ToolTipIcon.Info);
			}
			else if (command.Contains("APPLICATION")) {
				string exPath = command.Remove(0, 11);
				Process.Start("cmd", "/c start " + exPath);
				notifyIcon1.ShowBalloonTip(1000, "Application", "Application command received, executing now", ToolTipIcon.Info);
			}
			else if (command.Contains("CUSTOM")) {
				notifyIcon1.ShowBalloonTip(2500, "Custom", "Custom command received but cannot handle it", ToolTipIcon.Warning);
			}
			
		}

		private void exitToolStripMenuItem_Click(object sender, EventArgs e)
		{
			Application.Exit();
		}

		private void Form1_FormClosing(object sender, FormClosingEventArgs e)
		{
			File.WriteAllText(Environment.CurrentDirectory + "/" + "previouscommand.pc", previousCommand);
		}

	}


}
