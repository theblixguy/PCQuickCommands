using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace PCQuickCommandsClient
{
	public partial class Form2 : Form
	{
		public Form2()
		{
			InitializeComponent();
		}

		private void button1_Click(object sender, EventArgs e)
		{
			System.IO.File.WriteAllText(Environment.CurrentDirectory + "/" + "username.un", textBox1.Text);
			Application.Restart();
		}

		private void Form2_Load(object sender, EventArgs e)
		{

		}
	}
}
